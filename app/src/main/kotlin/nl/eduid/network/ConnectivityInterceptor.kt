package nl.eduid.network

import android.net.TrafficStats
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ConnectivityInterceptor : Interceptor {
    private companion object {
        const val NO_INTERNET = "No internet connection"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt())
        return try {
            chain.proceed(chain.request())
        } catch (e: UnknownHostException) {
            processException(e, chain)
        } catch (e: SocketTimeoutException) {
            processException(e, chain)
        }
    }

    private fun processException(e: Exception, chain: Interceptor.Chain): Response {
        Timber.d("$NO_INTERNET: exception ${e.message}", e)
        return Response
            .Builder()
            .code(HttpURLConnection.HTTP_CLIENT_TIMEOUT)
            .message(NO_INTERNET)
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .body(NO_INTERNET.toResponseBody(null))
            .build()
    }
}