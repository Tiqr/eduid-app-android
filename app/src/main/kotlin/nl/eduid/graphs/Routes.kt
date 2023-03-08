package nl.eduid.graphs

import androidx.navigation.NavType
import androidx.navigation.navArgument

object Graph {
    const val MAIN = "main_graph"
    const val HOME_PAGE = "home_page"
    const val REQUEST_EDU_ID_ACCOUNT = "request_edu_id_account"
    const val REQUEST_EDU_ID_DETAILS = "request_edu_id_details"
    const val REQUEST_EDU_ID_LINK_SENT = "request_edu_id_link_sent"

    const val START = "start"
    const val FIRST_TIME_DIALOG = "first_time_dialog"
    const val PERSONAL_INFO = "personal_info"
}

object OAuth {
    private const val route = "oauth_mobile_eduid"
    const val withPhoneConfirmArg = "confirm_phone_arg"
    val routeWithPhone = "$route/true"
    val routeWithoutPhone = "$route/false"
    val routeWithArgs = "$route/{$withPhoneConfirmArg}"
    val arguments = listOf(navArgument(withPhoneConfirmArg) {
        type = NavType.BoolType
        nullable = false
        defaultValue = true
    })
}

sealed class PhoneNumberRecovery(val route: String) {
    object RequestCode : PhoneNumberRecovery("phone_number_recover")
    object ConfirmCode : PhoneNumberRecovery("phone_number_confirm_code")
}

sealed class ExistingAccount(val route: String) {
    object EnrollWithQR : ExistingAccount("scan_registration")
    object RegistrationPinSetup : ExistingAccount("registration_pin_setup") {
        const val registrationChallengeArg = "registration_challenge_arg"

        val routeWithArgs = "$route/{$registrationChallengeArg}"
        val arguments = listOf(navArgument(registrationChallengeArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        })
    }
}

sealed class WithChallenge(val route: String) {
    companion object {
        const val challengeArg = "challenge_arg"
        const val pinArg = "pin_arg"
        const val isEnrolmentArg = "is_enrolment_arg"

        const val args = "{$challengeArg}/{$pinArg}/{$isEnrolmentArg}"
        val arguments = listOf(navArgument(challengeArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        }, navArgument(pinArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        }, navArgument(isEnrolmentArg) {
            type = NavType.BoolType
            nullable = false
            defaultValue = true
        })
    }

    object EnableBiometric : WithChallenge("enable_biometric") {
        val routeWithArgs = "$route/$args"
        fun buildRouteForEnrolment(encodedChallenge: String, pin: String): String =
            "$route/$encodedChallenge/$pin/true"

        fun buildRouteForAuthentication(encodedChallenge: String, pin: String): String =
            "$route/$encodedChallenge/$pin/false"

    }
}