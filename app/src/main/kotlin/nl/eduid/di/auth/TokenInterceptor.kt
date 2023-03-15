package nl.eduid.di.auth

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class TokenInterceptor @Inject constructor(private val tokenProvider: TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return if (request.header("Authorization") == null) {
            Timber.e("0 - Request does not have an Authorization header.")
            val token = tokenProvider.getToken()
            if (token != null) {
                Timber.e("1 - Have a non-null token. Adding authorization headers")
                chain.proceed(
                    request.newBuilder().addHeader("Authorization", "Bearer $token").build()
                )
            } else {
                Timber.e("1 - Token is null")
                chain.proceed(request)
            }
        } else {
            Timber.e("1 - Request does not have an authorization header")
            chain.proceed(request)
        }
    }
}