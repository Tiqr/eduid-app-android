package nl.eduid.screens.oauth

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import net.openid.appauth.*
import net.openid.appauth.ClientAuthentication.UnsupportedAuthenticationMethod
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import net.openid.appauth.connectivity.DefaultConnectionBuilder
import nl.eduid.R
import nl.eduid.di.assist.AuthenticationAssistant
import nl.eduid.di.repository.StorageRepository
import nl.eduid.screens.scan.ErrorData
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class OAuthViewModel @Inject constructor(
    private val repository: StorageRepository, private val assistant: AuthenticationAssistant,
    moshi: Moshi,
    @ApplicationContext context: Context,
) : ViewModel() {
    val isProcessing = MutableLiveData(true)
    val isReady: MutableLiveData<Unit?> = MutableLiveData(null)
    val errorData: MutableLiveData<ErrorData?> = MutableLiveData(null)
    private val customTabIntent: MutableLiveData<CustomTabsIntent?> = MutableLiveData(null)
    private val configAdapter: JsonAdapter<Configuration>
    private var configuration: Configuration = Configuration.EMPTY
    private val authState = repository.authState.asLiveData()
    private val clientId = repository.clientId.asLiveData()
    private val authRequest = repository.authRequest.asLiveData()
    var service: AuthorizationService? = null

    init {
        configAdapter = moshi.adapter(Configuration::class.java)
        prepareAppAuth(context)
    }

    fun prepareAppAuth(context: Context) = viewModelScope.launch {
        isProcessing.postValue(true)
        try {
            loadConfigurationFromResources(context.resources)
            initializeAppAuth(context)
            isReady.postValue(Unit)
        } catch (e: Exception) {
            errorData.postValue(
                ErrorData(
                    "Unexpected error", "Failed to initialize AppData. Please retry"
                )
            )
            Timber.e(e, "Failed to prepare AppAuth")
        }
        isProcessing.postValue(false)
    }

    fun createAuthorizationIntent(): Intent {
        val currentAuthRequest = authRequest.value
            ?: throw IllegalStateException("AuthorizationRequest not available when trying to create authorization request Intent")
        val availableService = service
            ?: throw IllegalStateException("AuthorizationService not available when trying to create authorization request Intent")
        val customTabIntent = warmupBrowser()
        return availableService.getAuthorizationRequestIntent(currentAuthRequest, customTabIntent)
    }

    fun continueWithFetchToken(intent: Intent?) = viewModelScope.launch {
        isProcessing.postValue(true)
        val currentAuthState = authState.value
        when {
            intent == null -> {
                errorData.postValue(
                    ErrorData("Authorization invalid", "Did not receive valid authentication.")
                )
            }
            currentAuthState == null -> {
                errorData.postValue(
                    ErrorData(
                        "Authorization failed",
                        "No authorization state retained - reauthorization required."
                    )
                )
            }
            currentAuthState.isAuthorized -> {
                //Already authorized, do nothing
            }
            else -> {
                val response = AuthorizationResponse.fromIntent(intent)
                val ex = AuthorizationException.fromIntent(intent)
                if (response != null || ex != null) {
                    currentAuthState.update(response, ex)
                    repository.saveCurrentAuthState(currentAuthState)
                }
                if (ex != null) {
                    errorData.postValue(
                        ErrorData(
                            "Authorization failed", ex.message ?: "Unexpected error, please retry"
                        )
                    )
                } else if (response?.authorizationCode != null) {
                    try {
                        exchangeAuthorizationCode(response)
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to exchange authorization code.")
                    }
                }
            }
        }
        isProcessing.postValue(false)
    }

    private suspend fun exchangeAuthorizationCode(response: AuthorizationResponse) {
        val currentAuthState = authState.value
            ?: throw IllegalStateException("AuthState not available when trying to exchange authorization code.")
        val availableService = service
            ?: throw IllegalStateException("AuthenticationService not available when trying to exchange authorization code.")

        val clientAuthentication = try {
            currentAuthState.clientAuthentication
        } catch (e: UnsupportedAuthenticationMethod) {
            errorData.postValue(
                ErrorData(
                    "Authorization failed", "Client authentication method is unsupported."
                )
            )
            throw IllegalStateException("AuthenticationService not available when trying to exchange authorization code.")
        }
        assistant.exchangeAuthorizationCode(response, clientAuthentication, availableService)
    }

    fun dismissError() {
        errorData.value = null
    }

    private fun loadConfigurationFromResources(resources: Resources): Configuration {
        val source =
            resources.openRawResource(R.raw.auth_config).bufferedReader().use { it.readText() }
        return try {
            configAdapter.fromJson(source) ?: Configuration.EMPTY
        } catch (e: IOException) {
            Timber.e(e, "Failed to parse configurations")
            Configuration.EMPTY
        }
    }

    private suspend fun recreateAuthorizationService(context: Context) {
        Timber.d("Discarding existing authorization service if available")
        service?.dispose()
        service = createAuthorizationService(context)
        repository.saveCurrentAuthRequest(null)
        customTabIntent.postValue(null)
    }

    private fun createAuthorizationService(context: Context): AuthorizationService {
        Timber.d("Creating AuthorizationService")
        val builder = AppAuthConfiguration.Builder()
        builder.setBrowserMatcher(
            BrowserAllowList(
                VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
                VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB,
                VersionedBrowserMatcher.FIREFOX_CUSTOM_TAB
            )
        )
        builder.setConnectionBuilder(DefaultConnectionBuilder.INSTANCE)

        return AuthorizationService(context, builder.build())
    }

    private suspend fun initializeAppAuth(context: Context) {
        Timber.d("Initializing AppAuth")
        recreateAuthorizationService(context)

        val currentAuthState = authState.value
        if (currentAuthState != null) {
            // configuration is already created, skip to client initialization
            Timber.d("auth config already established")
            initializeClient()
            return
        }
        // if we are not using discovery, build the authorization service configuration directly
        // from the static configuration values.
        if (configuration.discoveryUri != null) {
            Timber.d("Creating AuthorizationServiceConfiguration from statically known configuration")
            val serviceConfig = AuthorizationServiceConfiguration(
                configuration.authEndpointUri,
                configuration.tokenEndpointUri,
                configuration.registrationEndpointUri,
                configuration.endSessionEndpoint
            )
            repository.saveCurrentAuthState(AuthState(serviceConfig))
            initializeClient()
            return
        }
        val serviceConfig = assistant.retrieveOpenIdDiscoveryDoc(configuration)
        repository.saveCurrentAuthState(AuthState(serviceConfig))
        initializeClient()
    }

    private suspend fun initializeClient() {
        val staticClientId = configuration.clientId
        if (staticClientId != null) {
            Timber.d("Using static client id: $staticClientId")
            repository.saveClientId(staticClientId)
            initializeAuthRequest()
            return
        }
        val currentAuthState = authState.value ?: return
        val lastRegistrationResponse = currentAuthState.lastRegistrationResponse
        if (lastRegistrationResponse != null) {
            Timber.d("Using dynamic client id learned from previous registration: ${lastRegistrationResponse.clientId}")
            repository.saveClientId(lastRegistrationResponse.clientId)
            initializeAuthRequest()
            return
        }
        Timber.d("Dynamically registering client")
        val serviceConfiguration = currentAuthState.authorizationServiceConfiguration ?: return
        val registrationRequest = RegistrationRequest.Builder(
            serviceConfiguration, listOf(configuration.redirectUri)
        ).setTokenEndpointAuthenticationMethod(ClientSecretBasic.NAME).build()
        if (service != null) {
            val registrationResponse =
                assistant.performRegistrationRequest(registrationRequest, service!!)
            repository.saveClientId(registrationResponse.clientId)
            initializeAuthRequest()
        }
    }

    private suspend fun initializeAuthRequest() {
        createAuthRequest()
        warmupBrowser()
    }

    private fun warmupBrowser(): CustomTabsIntent {
        Timber.d("Warming up browser instance for auth request. Building custom tab intent")
        val currentAuthRequest = authRequest.value
            ?: throw IllegalStateException("AuthorizationRequest not available when trying to create CustomTabsIntent")
        val availableService = service
            ?: throw IllegalStateException("AuthorizationService not available when trying to create CustomTabsIntent")
        val intentBuilder =
            availableService.createCustomTabsIntentBuilder(currentAuthRequest.toUri())
        customTabIntent.value = intentBuilder?.build()
        return intentBuilder.build()
    }

    private suspend fun createAuthRequest(loginHint: String? = null) {
        val currentAuthState = authState.value
            ?: throw IllegalStateException("AuthState not available when trying to create AuthorizationRequest")
        val currentClientId = clientId.value
            ?: throw IllegalStateException("clientId not available when trying to create AuthorizationRequest")
        val currentConfiguration = currentAuthState.authorizationServiceConfiguration
            ?: throw IllegalStateException("AuthorizationServiceConfiguration not available when trying to create AuthorizationRequest")
        val authRequestBuilder = AuthorizationRequest.Builder(
            currentConfiguration,
            currentClientId,
            ResponseTypeValues.CODE,
            configuration.redirectUri
        ).setScope(configuration.scope)
        if (loginHint?.isEmpty() == false) {
            authRequestBuilder.setLoginHint(loginHint)
        }
        repository.saveCurrentAuthRequest(authRequestBuilder.build())
    }

    override fun onCleared() {
        service?.dispose()
    }
}
