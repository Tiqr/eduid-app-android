package nl.eduid

import androidx.navigation.NavType
import androidx.navigation.navArgument

object Graph {
    const val MAIN = "main_graph"
    const val SPLASH = "splash"
    const val ENROLL = "enroll"
    const val LOGIN = "login"
    const val SCAN_REGISTRATION = "scan_registration"
    const val REQUEST_EDU_ID_START = "request_edu_id_start"
    const val REQUEST_EDU_ID_DETAILS = "request_edu_id_details"
    const val REQUEST_EDU_ID_LINK_SENT = "request_edu_id_link_sent"
    const val REQUEST_EDU_ID_RECOVERY = "request_edu_id_recovery"
    const val REQUEST_EDU_ID_PIN = "request_edu_id_pin"
    const val START = "start"
    const val FIRST_TIME_DIALOG = "first_time_dialog"
    const val HOME_PAGE = "home_page"
    const val PERSONAL_INFO = "personal_info"
}

object RegistrationPinSetup {
    private const val route: String = "registration_pin_setup"
    const val registrationChallengeArg = "registration_challenge_arg"

    const val routeWithArgs = "${route}/{${registrationChallengeArg}}"
    val arguments = listOf(navArgument(registrationChallengeArg) {
        type = NavType.StringType
        nullable = false
        defaultValue = ""
    })

    fun buildRouteWithEncodedChallenge(encodedChallenge: String?): String {
        return "$route/$encodedChallenge"
    }
}

sealed class WithChallenge(val route: String) {
    companion object {
        const val challengeArg = "challenge_arg"
        const val pinArg = "pin_arg"
        const val isEnrolmentArg = "is_enrolment_arg"

        const val args = "{$challengeArg}/{${pinArg}}/{${isEnrolmentArg}}"
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

    object OAuth : WithChallenge("app_oauth") {
        val routeWithArgs = "$route/$args"
        fun buildRouteForEnrolment(encodedChallenge: String, pin: String): String =
            "$route/$encodedChallenge/$pin/true"

        fun buildRouteForAuthentication(encodedChallenge: String, pin: String): String =
            "$route/$encodedChallenge/$pin/false"

    }

    object EnableBiometric : WithChallenge("enable_biometric") {
        val routeWithArgs = "$route/$args"
        fun buildRouteForEnrolment(encodedChallenge: String, pin: String): String =
            "$route/$encodedChallenge/$pin/true"

        fun buildRouteForAuthentication(encodedChallenge: String, pin: String): String =
            "$route/$encodedChallenge/$pin/false"

    }
}