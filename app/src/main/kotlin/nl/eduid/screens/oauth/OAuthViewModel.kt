package nl.eduid.screens.oauth

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.*
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
import nl.eduid.BaseViewModel
import nl.eduid.R
import nl.eduid.WithChallenge
import nl.eduid.di.assist.AuthenticationAssistant
import nl.eduid.di.repository.StorageRepository
import nl.eduid.screens.scan.ErrorData
import org.tiqr.core.util.extensions.biometricUsable
import org.tiqr.data.model.AuthenticationChallenge
import org.tiqr.data.model.Challenge
import org.tiqr.data.model.EnrollmentChallenge
import timber.log.Timber
import java.io.IOException
import java.net.URLDecoder
import javax.inject.Inject


@HiltViewModel
class OAuthViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: StorageRepository,
    private val assistant: AuthenticationAssistant,
    moshi: Moshi,
    @ApplicationContext context: Context,
) : BaseViewModel(moshi) {
    val uiState: MutableLiveData<UiState> = MutableLiveData(UiState(OAuthStep.Loading))

    private var service: AuthorizationService? = null
    private val configAdapter: JsonAdapter<Configuration>
    private var configuration: Configuration = Configuration.EMPTY
    private val authState = repository.authState.asLiveData()
    private val clientId = repository.clientId.asLiveData()
    private val authRequest = repository.authRequest.asLiveData()
    private val challenge: Challenge?
    private val pin: String
    private val promptBiometric: Boolean?

    init {
        configAdapter = moshi.adapter(Configuration::class.java)
        val isEnrolment = savedStateHandle.get<Boolean>(WithChallenge.isEnrolmentArg) ?: true
        val challengeArg = savedStateHandle.get<String>(WithChallenge.challengeArg) ?: ""
        val decoded = try {
            URLDecoder.decode(challengeArg, Charsets.UTF_8.name())
        } catch (e: Exception) {
            ""
        }
        val adapter = if (isEnrolment) {
            moshi.adapter(EnrollmentChallenge::class.java)
        } else {
            moshi.adapter(AuthenticationChallenge::class.java)
        }
        pin = savedStateHandle.get<String>(WithChallenge.pinArg) ?: ""
        challenge = try {
            adapter.fromJson(decoded)
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse challenge")
            null
        }
        promptBiometric =
            context.biometricUsable() && challenge?.identity?.biometricOfferUpgrade == true
        prepareAppAuth(context)
    }

    fun prepareAppAuth(context: Context) = viewModelScope.launch {
        uiState.postValue(UiState(OAuthStep.Loading))
        try {
            loadConfigurationFromResources(context.resources)
            initializeAppAuth(context)
            uiState.postValue(UiState(OAuthStep.Initialized))
        } catch (e: Exception) {
            setError(
                title = "Unexpected error", message = "Failed to initialize AppData. Please retry"
            )
            Timber.e(e, "Failed to prepare AppAuth")
        }
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
        val currentAuthState = authState.value
        when {
            intent == null -> {
                setError(
                    title = "Authorization invalid",
                    message = "Did not receive valid authentication."
                )
            }
            currentAuthState == null -> {
                setError(
                    title = "Authorization failed",
                    message = "No authorization state retained - reauthorization required."
                )
            }
            currentAuthState.isAuthorized -> {
                uiState.postValue(
                    UiState(
                        oauthStep = OAuthStep.Authorized,
                        error = null,
                        promptBiometric = promptBiometric
                    )
                )
            }
            else -> {
                val response = AuthorizationResponse.fromIntent(intent)
                val ex = AuthorizationException.fromIntent(intent)
                if (response != null || ex != null) {
                    currentAuthState.update(response, ex)
                    repository.saveCurrentAuthState(currentAuthState)
                }
                if (response?.authorizationCode != null) {
                    try {
                        uiState.postValue(UiState(OAuthStep.ExchangingTokenRequest))
                        val tokenResponse = exchangeAuthorizationCode(response)
                        currentAuthState.update(tokenResponse, ex)
                        repository.saveCurrentAuthState(currentAuthState)
                        if (currentAuthState.isAuthorized) {
                            uiState.postValue(
                                UiState(
                                    oauthStep = OAuthStep.Authorized,
                                    error = null,
                                    promptBiometric = promptBiometric
                                )
                            )
                        } else {
                            setError(
                                title = "Authorization failed",
                                message = "Authorization code exchange failed"
                            )
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to exchange authorization code.")
                        setError(
                            title = "Authorization failed",
                            message = "Failed to exchange authorization code."
                        )
                    }
                } else if (ex != null) {
                    setError(
                        title = "Authorization failed",
                        message = ex.message ?: "Unexpected error, please retry"
                    )
                } else {
                    setError(
                        title = "Authorization failed",
                        message = "No Authorization state retained - reauthorization required."
                    )
                }
            }
        }
    }

    private fun setError(title: String, message: String) = uiState.postValue(
        UiState(OAuthStep.Error, ErrorData(title, message))
    )

    private suspend fun exchangeAuthorizationCode(response: AuthorizationResponse): TokenResponse {
        val currentAuthState = authState.value
            ?: throw IllegalStateException("AuthState not available when trying to exchange authorization code.")
        val availableService = service
            ?: throw IllegalStateException("AuthenticationService not available when trying to exchange authorization code.")

        val clientAuthentication = try {
            currentAuthState.clientAuthentication
        } catch (e: UnsupportedAuthenticationMethod) {
            throw IllegalStateException("AuthenticationService not available when trying to exchange authorization code.")
        }
        return assistant.exchangeAuthorizationCode(response, clientAuthentication, availableService)
    }

    fun dismissError() {
        val currentUiState = uiState.value ?: return
        uiState.value = currentUiState.copy(error = null)
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
            createAuthRequest()
            return
        }
        val currentAuthState = authState.value ?: return
        val lastRegistrationResponse = currentAuthState.lastRegistrationResponse
        if (lastRegistrationResponse != null) {
            Timber.d("Using dynamic client id learned from previous registration: ${lastRegistrationResponse.clientId}")
            repository.saveClientId(lastRegistrationResponse.clientId)
            createAuthRequest()
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
            createAuthRequest()
        }
    }

    private fun warmupBrowser(): CustomTabsIntent {
        Timber.d("Warming up browser instance for auth request. Building custom tab intent")
        val currentAuthRequest = authRequest.value
            ?: throw IllegalStateException("AuthorizationRequest not available when trying to create CustomTabsIntent")
        val availableService = service
            ?: throw IllegalStateException("AuthorizationService not available when trying to create CustomTabsIntent")
        val intentBuilder =
            availableService.createCustomTabsIntentBuilder(currentAuthRequest.toUri())
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
