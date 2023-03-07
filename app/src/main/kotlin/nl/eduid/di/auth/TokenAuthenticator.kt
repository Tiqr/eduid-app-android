package nl.eduid.di.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenProvider: TokenProvider
) : Authenticator {
    private var currentToken: String? = null

    override fun authenticate(route: Route?, response: Response): Request? = synchronized(this) {
        Timber.d("A - Received authentication challenge from server")
        val token = tokenProvider.refreshToken()
        val previousToken = currentToken

        return if (token != previousToken) {
            Timber.d("B - Adding authorization to header")
            currentToken = token
            response.request.newBuilder().header(
                "Authorization", "Bearer $token"
            ).build()
        } else {
            Timber.i("B - Not adding the token since it didn't change.")
            null
        }
    }
}