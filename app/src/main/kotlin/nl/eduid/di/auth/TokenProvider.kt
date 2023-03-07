package nl.eduid.di.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import nl.eduid.di.repository.StorageRepository
import timber.log.Timber
import java.util.*
import kotlin.NoSuchElementException

class TokenProvider(private val repository: StorageRepository) {
    private var token: String? = null

    fun getToken(): String? = runBlocking(Dispatchers.IO) {
        token ?: refreshToken()
    }

    fun refreshToken(): String? = runBlocking(Dispatchers.IO) {
        token = if (!repository.isAuthorized.first()) {
            Timber.d("\tNot authorized. Token missing")
            null
        } else {
            try {
                //Todo: to an actual token refresh here
                val authState = repository.authState.firstOrNull()
                val expiresAt = Date(authState?.accessTokenExpirationTime ?: 0)
                val accessToken = authState?.accessToken
                Timber.d(
                    "\tIs authorized. Token expires at $expiresAt. \n\t Token: $accessToken"
                )
                accessToken
            } catch (ex: NoSuchElementException) {
                Timber.w(ex, "\tError refreshing token")
                null
            }
        }
        token
    }
}