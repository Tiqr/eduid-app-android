package nl.eduid.di.auth

import android.content.Context
import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import nl.eduid.di.assist.AuthenticationAssistant
import nl.eduid.di.repository.StorageRepository
import timber.log.Timber
import java.util.*

class TokenProvider(
    private val repository: StorageRepository,
    private val assistant: AuthenticationAssistant,
    context: Context
) {
    private var token: String? = null
    private val service = AuthenticationAssistant.createAuthorizationService(context)

    @WorkerThread
    fun getToken(): String? = runBlocking(Dispatchers.IO) {
        token ?: refreshToken()
    }

    @WorkerThread
    fun refreshToken(): String? = runBlocking(Dispatchers.IO) {
        try {
            token = if (!repository.isAuthorized.first()) {
                Timber.w("Not authorized. Token missing")
                null
            } else {
                val authState = repository.authState.first()
                if (authState != null) {
                    val expiresAt = Date(authState.accessTokenExpirationTime ?: 0)
                    val needTokenRefresh = authState.needsTokenRefresh
                    if (needTokenRefresh) {
                        try {
                            val token = assistant.refreshToken(authState, service)
                            authState.update(token, null)
                            repository.saveCurrentAuthState(authState)
                        } catch (ex: Exception) {
                            Timber.e(ex, "Failed to refresh token")
                        }
                    }
                    val accessToken = authState.accessToken
                    Timber.d(
                        "\tIs authorized. Token expires at $expiresAt. Need token refresh: $needTokenRefresh \n\t Token: $accessToken"
                    )
                    accessToken
                } else {
                    null
                }
            }
        } catch (ex: NoSuchElementException) {
            Timber.w(ex, "Error refreshing token")
        }

        token
    }

    fun disposeService() {
        service.dispose()
    }
}