package nl.eduid.di.assist

class UnauthorizedException(
    message: String = "Unauthorized request failure",
    cause: Throwable? = null
) : Throwable(message, cause)

