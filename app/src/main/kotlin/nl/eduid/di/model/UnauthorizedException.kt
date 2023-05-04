package nl.eduid.di.model

class UnauthorizedException(
    message: String = "Unauthorized request failure",
    cause: Throwable? = null
) : Throwable(message, cause)