package nl.eduid.di.assist

import android.content.Context
import android.net.Uri
import net.openid.appauth.*
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import net.openid.appauth.connectivity.DefaultConnectionBuilder
import nl.eduid.screens.oauth.Configuration
import timber.log.Timber
import kotlin.coroutines.suspendCoroutine


class AuthenticationAssistant {

    suspend fun retrieveOpenIdDiscoveryDoc(configuration: Configuration): AuthorizationServiceConfiguration =
        suspendCoroutine { continuation ->
            AuthorizationServiceConfiguration.fetchFromUrl(
                configuration.discoveryUri ?: Uri.EMPTY,
                { config: AuthorizationServiceConfiguration?, ex: AuthorizationException? ->
                    when {
                        ex != null -> {
                            Timber.e(ex, "Failed to retrieve discovery document")
                            continuation.resumeWith(Result.failure(ex))
                        }

                        config != null -> {
                            continuation.resumeWith(Result.success(config))
                        }

                        else -> {
                            continuation.resumeWith(Result.failure(RuntimeException("Could not complete discovery")))
                        }
                    }
                },
                DefaultConnectionBuilder.INSTANCE
            )
        }

    suspend fun performRegistrationRequest(
        registrationRequest: RegistrationRequest, service: AuthorizationService,
    ): RegistrationResponse = suspendCoroutine { continuation ->
        service.performRegistrationRequest(
            registrationRequest
        ) { response: RegistrationResponse?, ex: AuthorizationException? ->
            when {
                ex != null -> {
                    Timber.e(ex, "Failed to dynamically register client")
                    continuation.resumeWith(Result.failure(ex))
                }

                response != null -> {
                    continuation.resumeWith(Result.success(response))
                }

                else -> {
                    continuation.resumeWith(Result.failure(RuntimeException("Could not complete client dynamic registration")))
                }
            }

        }
    }

    suspend fun exchangeAuthorizationCode(
        response: AuthorizationResponse,
        clientAuthentication: ClientAuthentication,
        service: AuthorizationService,
    ): TokenResponse = suspendCoroutine { continuation ->
        service.performTokenRequest(
            /* request = */
            response.createTokenExchangeRequest(),
            /* clientAuthentication = */
            clientAuthentication,
        ) { tokenResponse, ex ->
            when {
                ex != null -> {
                    Timber.e(ex, "Failed to exchange authorization code")
                    continuation.resumeWith(Result.failure(ex))
                }

                tokenResponse != null -> {
                    continuation.resumeWith(Result.success(tokenResponse))
                }

                else -> {
                    continuation.resumeWith(Result.failure(RuntimeException("Could not complete client dynamic registration")))
                }
            }
        }
    }

    suspend fun refreshToken(authState: AuthState, service: AuthorizationService): TokenResponse =
        suspendCoroutine { continuation ->
            val refreshTokenRequest = authState.createTokenRefreshRequest()
            service.performTokenRequest(refreshTokenRequest) { tokenResponse, ex ->
                when {
                    ex != null -> {
                        Timber.e(ex, "Failed to refresh token")
                        continuation.resumeWith(Result.failure(ex))
                    }

                    tokenResponse != null -> {
                        continuation.resumeWith(Result.success(tokenResponse))
                    }

                    else -> {
                        continuation.resumeWith(Result.failure(RuntimeException("Could not complete token refresh")))
                    }
                }
            }
        }

    companion object {
        fun createAuthorizationService(context: Context): AuthorizationService {
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
    }
}