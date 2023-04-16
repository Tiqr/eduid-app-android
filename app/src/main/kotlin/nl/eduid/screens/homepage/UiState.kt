package nl.eduid.screens.homepage

import nl.eduid.ErrorData
import org.tiqr.data.model.Challenge

data class UiState(
    val inProgress: Boolean = false,
    val currentChallenge: Challenge? = null,
    val promptForAuth: Unit? = null,
    val errorData: ErrorData? = null,
    val isEnrolled: IsEnrolled = IsEnrolled.Unknown,
) {
    /** There is a valid challenge if:
     * 1 - The VM is not busy processing something
     * 2 - There is no known error state
     * 3 - There is a non-null [currentChallenge]
     * 4 - The app did not prompt for an OAuth flow
     * */
    fun haveValidChallenge() =
        !inProgress && errorData == null && currentChallenge != null && promptForAuth == null

    fun shouldTriggerAutomaticStartEnrollmentAfterOauth() =
        promptForAuth == null && !inProgress && errorData == null && currentChallenge == null
}

sealed class IsEnrolled {
    object Yes : IsEnrolled()
    object No : IsEnrolled()
    object Unknown : IsEnrolled()
}