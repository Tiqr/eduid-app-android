package nl.eduid.di.assist

import retrofit2.Response
import timber.log.Timber
import java.io.IOException

inline fun <reified T> processResponse(response: Response<out T>): Result<T> = with(response) {
    if (isSuccessful) {
        val data: T? = body()
        if (data != null) {
            Result.success(data)
        } else {
            Timber.e("Expected data in response body, but body is empty.")
            Result.failure(EmptyResponseBodyException)
        }
    } else if (code() == java.net.HttpURLConnection.HTTP_UNAUTHORIZED) {
        Result.failure(UnauthorizedException("Unauthorized getUserDetails call"))
    } else {
        val exception = "${code()}/${message()} : ${errorBody()?.string()}"
        Timber.e("Failed to get ${T::class.java.name}. Exception: $exception")
        Result.failure(IOException(exception))
    }
}

/**
 * Same as [processResponse], for calls which do not return a body. Non-401 failures are wrapped in a
 * [RequestFailedException] so callers can react to specific HTTP status codes.
 */
fun processEmptyResponse(response: Response<*>): Result<Unit> = with(response) {
    when {
        isSuccessful -> Result.success(Unit)
        code() == java.net.HttpURLConnection.HTTP_UNAUTHORIZED -> Result.failure(UnauthorizedException("Unauthorized call"))
        else -> {
            val exception = "${code()}/${message()} : ${errorBody()?.string()}"
            Timber.e("Request failed. Exception: $exception")
            Result.failure(RequestFailedException(code(), exception))
        }
    }
}