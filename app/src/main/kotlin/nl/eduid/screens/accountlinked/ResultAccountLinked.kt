package nl.eduid.screens.accountlinked

import android.net.Uri

sealed interface ResultAccountLinked {
    data class Success(val institutionId: String?) : ResultAccountLinked
    data class FailedAlreadyLinkedResult(val withEmail: String) : ResultAccountLinked
    data object FailedExpired : ResultAccountLinked

    companion object {
        fun fromRedirectUrl(uri: Uri) = when {
            uri.path?.contains("expired", true) ?: false -> FailedExpired
            uri.path?.contains("already-linked")
                ?: false -> FailedAlreadyLinkedResult(uri.getQueryParameter("email") ?: "")
            else -> {
                val institutionId = uri.getQueryParameter("institution")
                Success(institutionId)
            }
        }
    }
}