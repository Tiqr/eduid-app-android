package nl.eduid.di.assist

import nl.eduid.ErrorData
import nl.eduid.R

/**
 * Helper function to map the different [Throwable] failures to a language dependent user readable error message. If the Throwable is null
 * then the message is a generic error message.
 * */
fun Throwable?.toErrorData() = when (this) {
    is DataFetchException -> ErrorData(
        titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
        messageId = R.string.ResponseErrors_GeneralRequestError_COPY,
    )

    is UnauthorizedException -> ErrorData(
        titleId = R.string.ResponseErrors_Auth_Invalid_Title_COPY,
        messageId = R.string.ResponseErrors_UnauthorizedText_COPY
    )

    EmptyResponseBodyException -> ErrorData(
        titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
        messageId = R.string.ResponseErrors_GeneralRequestError_COPY,
    )
    null -> null
    // If we reach here, we're truly and completely lost and we can't find our way back.
    else -> ErrorData(
        titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
        messageId = R.string.ResponseErrors_GeneralRequestError_COPY,
    )
}