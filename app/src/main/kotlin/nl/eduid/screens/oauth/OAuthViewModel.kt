package nl.eduid.screens.oauth

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Base64
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication.UnsupportedAuthenticationMethod
import net.openid.appauth.ClientSecretBasic
import net.openid.appauth.RegistrationRequest
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenResponse
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.AuthenticationAssistant
import nl.eduid.di.repository.StorageRepository
import nl.eduid.env.EnvironmentProvider
import timber.log.Timber
import java.io.IOException
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject


@HiltViewModel
class OAuthViewModel @Inject constructor(
    private val repository: StorageRepository,
    private val assistant: AuthenticationAssistant,
    moshi: Moshi,
    @ApplicationContext context: Context,
) : ViewModel() {
    var uiState by mutableStateOf(UiState(OAuthStep.Loading))
        private set
    private val usePKCE = true
    private var service: AuthorizationService? = null
    private val configAdapter: JsonAdapter<Configuration>
    private var configuration: Configuration = Configuration.EMPTY

    init {
        configAdapter = moshi.adapter(Configuration::class.java)
        prepareAppAuth(context)
    }

    fun prepareAppAuth(context: Context) = viewModelScope.launch {
        uiState = UiState(OAuthStep.Loading)
        try {
            configuration = loadConfigurationFromResources(context.resources)
            checkIfConfigurationChanged()
            initializeAppAuth(context)
            val authorizationIntent = createAuthorizationIntent()
            uiState = UiState(OAuthStep.Initialized(authorizationIntent))
        } catch (e: Exception) {
            val argument = e.message ?: e.localizedMessage
            uiState = UiState(
                OAuthStep.Error, ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = if (argument == null) R.string.AppData_InitializationError_Generic_COPY else R.string.AppData_InitializationError_WithException_COPY,
                    messageArg = argument
                )
            )
            Timber.e(e, "Failed to prepare AppAuth")
        }
    }

    private suspend fun createAuthorizationIntent(): Intent {
        val currentAuthRequest = repository.authRequest.first()
            ?: throw IllegalStateException("AuthorizationRequest not available when trying to create authorization request Intent")
        val availableService = service
            ?: throw IllegalStateException("AuthorizationService not available when trying to create authorization request Intent")
        val customTabIntent = warmupBrowser()
        return availableService.getAuthorizationRequestIntent(currentAuthRequest, customTabIntent)
    }

    fun continueWithFetchToken(intent: Intent?) = viewModelScope.launch {
        Timber.d("Continue with fetch token")
        val currentAuthState = repository.authState.first()
        when {
            intent == null -> {
                uiState = UiState(
                    OAuthStep.Error, ErrorData(
                        titleId = R.string.ResponseErrors_Auth_Invalid_Title_COPY,
                        messageId = R.string.ResponseErrors_Auth_Invalid_Description_COPY,
                    )
                )
            }

            currentAuthState == null -> {
                uiState = UiState(
                    OAuthStep.Error, ErrorData(
                        titleId = R.string.ResponseErrors_Auth_Failed_Title_COPY,
                        messageId = R.string.ResponseErrors_Auth_Failed_Description_COPY,
                    )
                )
            }

            currentAuthState.isAuthorized -> {
                Timber.d("Current state is already authorized.")
                uiState =
                    UiState(
                        oauthStep = OAuthStep.Authorized,
                        error = null,
                    )
            }

            else -> {
                val response = AuthorizationResponse.fromIntent(intent)
                val ex = AuthorizationException.fromIntent(intent)
                Timber.d("Processing authorization response from intent.")
                if (response != null || ex != null) {
                    currentAuthState.update(response, ex)
                    repository.saveCurrentAuthState(currentAuthState)
                }
                if (response?.authorizationCode != null) {
                    try {
                        uiState = UiState(OAuthStep.ExchangingTokenRequest)
                        val tokenResponse = exchangeAuthorizationCode(response)
                        currentAuthState.update(tokenResponse, ex)
                        repository.saveCurrentAuthState(currentAuthState)
                        if (currentAuthState.isAuthorized) {
                            uiState =
                                UiState(
                                    oauthStep = OAuthStep.Authorized,
                                    error = null,
                                )
                        } else {
                            uiState = UiState(
                                OAuthStep.Error, ErrorData(
                                    titleId = R.string.ResponseErrors_Auth_Failed_Title_COPY,
                                    messageId = R.string.ResponseErrors_Auth_Failed_TokenFailedDescription_Generic_COPY,
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to exchange authorization code.")
                        val arg = "${e.javaClass.simpleName}: ${e.message}"
                        uiState = UiState(
                            OAuthStep.Error, ErrorData(
                                titleId = R.string.ResponseErrors_Auth_Failed_Title_COPY,
                                messageId = R.string.ResponseErrors_Auth_Failed_TokenFailedDescription_WithException_COPY,
                                messageArg = arg
                            )
                        )
                    }
                } else if (ex != null) {
                    val arg = "${ex.javaClass.simpleName}: ${ex.message}"
                    uiState = UiState(
                        OAuthStep.Error, ErrorData(
                            titleId = R.string.ResponseErrors_Auth_Failed_Title_COPY,
                            messageId = R.string.Generic_RequestError_Description_COPY,
                            messageArg = arg
                        )
                    )
                } else {
                    uiState = UiState(
                        OAuthStep.Error, ErrorData(
                            titleId = R.string.ResponseErrors_Auth_Failed_Title_COPY,
                            messageId = R.string.ResponseErrors_Auth_Failed_Description_COPY,
                        )
                    )
                }
            }
        }
    }

    private suspend fun checkIfConfigurationChanged() {
        val lastKnownHash = repository.lastKnownConfigHash.first()
        if (configuration.hashCode() != lastKnownHash) {
            Timber.d("Configuration change detected, discarding old state")
            repository.saveCurrentAuthState(AuthState())
            repository.acceptNewConfiguration(configuration.hashCode())
        }
    }

    private suspend fun exchangeAuthorizationCode(response: AuthorizationResponse): TokenResponse {
        val currentAuthState = repository.authState.first()
            ?: throw IllegalStateException("AuthState not available when trying to exchange authorization code.")
        val availableService = service
            ?: throw IllegalStateException("AuthenticationService not available when trying to exchange authorization code.")

        val clientAuthentication = try {
            currentAuthState.clientAuthentication
        } catch (e: UnsupportedAuthenticationMethod) {
            throw IllegalStateException("AuthenticationService not available when trying to exchange authorization code.")
        }
        return assistant.exchangeAuthorizationCode(
            response, clientAuthentication, availableService
        )
    }

    fun dismissError() {
        uiState = uiState.copy(error = null)
    }

    private fun loadConfigurationFromResources(resources: Resources): Configuration {
        val source =
            resources.openRawResource(EnvironmentProvider.getCurrent().authConfig).bufferedReader()
                .use { it.readText() }
        return try {
            configAdapter.fromJson(source) ?: Configuration.EMPTY
        } catch (e: IOException) {
            Timber.e(e, "Failed to parse configurations")
            Configuration.EMPTY
        }
    }

    private suspend fun recreateAuthorizationService(context: Context) {
        service?.dispose()
        service = AuthenticationAssistant.createAuthorizationService(context)
        repository.saveCurrentAuthRequest(null)
    }

    private suspend fun initializeAppAuth(context: Context) {
        Timber.d("Initializing AppAuth")
        recreateAuthorizationService(context)

        val currentAuthState = repository.authState.first()
        if (currentAuthState?.authorizationServiceConfiguration != null) {
            // configuration is already created, skip to client initialization
            Timber.d("authorization service configuration already established")
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
                configuration.endSessionEndpointUri
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
        val currentAuthState = repository.authState.first() ?: return
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

    private suspend fun warmupBrowser(): CustomTabsIntent {
        Timber.d("Warming up browser instance for auth request. Building custom tab intent")
        val currentAuthRequest = repository.authRequest.first()
            ?: throw IllegalStateException("AuthorizationRequest not available when trying to create CustomTabsIntent")
        val availableService = service
            ?: throw IllegalStateException("AuthorizationService not available when trying to create CustomTabsIntent")
        val intentBuilder =
            availableService.createCustomTabsIntentBuilder(currentAuthRequest.toUri())
        return intentBuilder.build()
    }

    private suspend fun createAuthRequest(loginHint: String? = null) {
        if (usePKCE) {
            createAuthRequestViaPKCE(loginHint)
        } else {
            createAuthRequestViaAuthenticationFlow(loginHint)
        }
    }

    private suspend fun createAuthRequestViaAuthenticationFlow(loginHint: String? = null) {
        val currentAuthState = repository.authState.first()
            ?: throw IllegalStateException("AuthState not available when trying to create AuthorizationRequest")
        val currentClientId = repository.clientId.first()
            ?: throw IllegalStateException("clientId not available when trying to create AuthorizationRequest")
        val currentConfiguration = currentAuthState.authorizationServiceConfiguration
            ?: throw IllegalStateException("AuthorizationServiceConfiguration not available when trying to create AuthorizationRequest")
        val authRequestBuilder = AuthorizationRequest.Builder(
            currentConfiguration,
            currentClientId,
            ResponseTypeValues.CODE,
            configuration.redirectUri
        ).setScopes(configuration.scope, "openid", "profile", "email")
        if (loginHint?.isEmpty() == false) {
            authRequestBuilder.setLoginHint(loginHint)
        }
        repository.saveCurrentAuthRequest(authRequestBuilder.build())
    }

    private suspend fun createAuthRequestViaPKCE(loginHint: String? = null) {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(64)
        secureRandom.nextBytes(bytes)

        val encoding = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
        val codeVerifier = Base64.encodeToString(bytes, encoding)
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(codeVerifier.toByteArray())
        val codeChallenge = Base64.encodeToString(hash, encoding)

        val currentAuthState = repository.authState.first()
            ?: throw IllegalStateException("AuthState not available when trying to create AuthorizationRequest")
        val currentClientId = repository.clientId.first()
            ?: throw IllegalStateException("clientId not available when trying to create AuthorizationRequest")
        val currentConfiguration = currentAuthState.authorizationServiceConfiguration
            ?: throw IllegalStateException("AuthorizationServiceConfiguration not available when trying to create AuthorizationRequest")
        val authRequestBuilder = AuthorizationRequest.Builder(
            currentConfiguration,
            currentClientId,
            ResponseTypeValues.CODE,
            configuration.redirectUri
        ).setCodeVerifier(
            codeVerifier,
            codeChallenge,
            "S256"
        ).setScope(configuration.scope)
        repository.saveCurrentAuthRequest(authRequestBuilder.build())
    }

    override fun onCleared() {
        service?.dispose()
    }

    fun authorizationLaunched() {
        uiState = uiState.copy(oauthStep = OAuthStep.Launched)
    }
}
