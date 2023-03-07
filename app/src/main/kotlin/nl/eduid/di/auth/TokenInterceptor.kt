package nl.eduid.di.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(private val tokenProvider: TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.header("Authorization") == null) {
            return runBlocking(Dispatchers.IO) {
                val token = tokenProvider.getToken()
                if (token != null) {
                    chain.proceed(
                        request.newBuilder().addHeader("Authorization", "Bearer $token").build()
                    )
                } else {
                    chain.proceed(request)
                }
            }
        }
        return chain.proceed(request)
    }
}