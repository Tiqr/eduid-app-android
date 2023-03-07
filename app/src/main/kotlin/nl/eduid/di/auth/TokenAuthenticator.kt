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
        val token = tokenProvider.refreshToken()
        val previousToken = currentToken

        return if (token != previousToken) {
            Timber.e("A - Adding authorization to header")
            currentToken = token
            response.request.newBuilder().header(
                "Authorization",
                "Bearer $token"
            ).build()
        } else {
            Timber.e("A - Not adding any token. Current token: ${currentToken != null}")
            null
        }
    }
}