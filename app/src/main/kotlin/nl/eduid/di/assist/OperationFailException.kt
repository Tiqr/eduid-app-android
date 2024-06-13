package nl.eduid.di.assist

class OperationFailException(
    operation: String = "",
    message: String? = null,
    cause: Throwable? = null,
) :
    Throwable(message = "Cannot complete $operation. ${message.orEmpty()}", cause = cause)