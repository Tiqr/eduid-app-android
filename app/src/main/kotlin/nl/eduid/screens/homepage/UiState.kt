package nl.eduid.screens.homepage

import nl.eduid.screens.scan.ErrorData
import org.tiqr.data.model.Challenge

data class UiState(
    val inProgress: Boolean = false,
    val currentChallenge: Challenge? = null,
    val promptForAuth: Unit? = null,
    val errorData: ErrorData? = null,
    val isEnrolled: IsEnrolled = IsEnrolled.Unknown,
) {
    fun haveValidChallenge() = !inProgress && errorData == null && currentChallenge != null
}

sealed class IsEnrolled {
    object Yes : IsEnrolled()
    object No : IsEnrolled()
    object Unknown : IsEnrolled()
}