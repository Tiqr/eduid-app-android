package nl.eduid.di.assist

class DataFetchException(message: String? = null, cause: Throwable? = null) :
    Throwable(message = message.orEmpty(), cause = cause)