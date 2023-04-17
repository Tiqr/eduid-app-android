package nl.eduid.screens.personalinfo

import android.content.Intent
import nl.eduid.ErrorData

data class UiState(
    val personalInfo: PersonalInfo = PersonalInfo(),
    val linkUrl: Intent? = null,
    val isLoading: Boolean = false,
    val errorData: ErrorData? = null,
) {
    fun haveValidLinkIntent() = !isLoading && errorData == null && linkUrl != null
}