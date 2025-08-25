package nl.eduid.network

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class ErrorConverter(private val converter: Converter<ResponseBody, ApiError>) {

    fun parse(errorBody: ResponseBody): ApiError? {
        return try {
            converter.convert(errorBody)
        } catch (exception: IOException) {
            null
        }
    }

    private fun convertHttpExceptionToApiError(httpException: HttpException?): ApiError? {
        return httpException?.response()?.errorBody()?.let { parse(it) }
    }
}
