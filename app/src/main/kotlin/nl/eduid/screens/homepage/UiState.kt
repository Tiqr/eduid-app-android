package nl.eduid.screens.homepage

import nl.eduid.ErrorData
import org.tiqr.data.model.Challenge
import org.tiqr.data.model.Identity

data class UiState(
    val inProgress: Boolean = false,
    val currentChallenge: Challenge? = null,
    val promptForAuth: Unit? = null,
    val errorData: ErrorData? = null,
    val isEnrolled: IsEnrolled = IsEnrolled.Unknown,
    val preEnrollCheck: PreEnrollCheck? = null,
    val deactivateFor: DeactivateFor? = null,
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

data class DeactivateFor(val phoneNumber: String)

/**
 * States that map the combination of local TIQR enrolment and eduid account information.
 * The [UserDetails] identifier maps to the [Identity] identifier (aka the local TIQR secret)
 * In order for the enrolment to start the following must be true:
 *  - No local TIQR secret must existing that has the same identifier as the [UserDetails]: no
 * local enrolment exists for this eduid account
 *  - [UserDetails] must not have app as a login option: the account already has another device
 * registered for 2FA
 * */
sealed class PreEnrollCheck {
    /**
     * This eduid account already has an app registered for 2FA on another device
     * */
    object DeactivateExisting : PreEnrollCheck()

    /**
     * This app and this account are already connected for 2FA. Should not be possible to
     * trigger because the login button is not accessible while there is a local key
     * */
    object AlreadyCompleted : PreEnrollCheck()

    /**
     * This app and this account were previously connected for 2FA, but the registration did
     * not complete or the key was invalidated in the meantime. Should not be possible to
     * trigger because the login button is not accessible while there is a local key
     * */
    object Incomplete : PreEnrollCheck()
}
