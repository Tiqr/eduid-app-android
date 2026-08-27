package nl.eduid.di.assist

class RequestFailedException(
    val httpCode: Int,
    message: String? = null,
) : Throwable(message)
