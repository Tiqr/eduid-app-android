package nl.eduid.screens.firsttimedialog

import android.content.Intent
import nl.eduid.screens.scan.ErrorData

data class UiState(
    val linkUrl: Intent? = null,
    val inProgress: Boolean = false,
    val errorData: ErrorData? = null
) {
    fun haveValidLinkIntent() = !inProgress && errorData == null && linkUrl != null
}