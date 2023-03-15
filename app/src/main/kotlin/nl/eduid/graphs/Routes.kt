package nl.eduid.graphs

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder

object Graph {
    const val HOME_PAGE = "home_page"
    const val REQUEST_EDU_ID_ACCOUNT = "request_edu_id_account"
    const val REQUEST_EDU_ID_FORM = "request_edu_id_details"

    const val START = "start"
    const val FIRST_TIME_DIALOG = "first_time_dialog"
    const val PERSONAL_INFO = "personal_info"
}

object RequestEduIdCreated {
    private const val route = "request_edu_id_created"
    private const val isCreatedArg = "new"
    const val routeWithArgs = "${route}/{${isCreatedArg}}"
    val uriPattern = "eduid:///client/mobile/created?new={${isCreatedArg}}"

    fun decodeFromEntry(entry: NavBackStackEntry): Boolean =
        (entry.arguments?.getString(isCreatedArg, "true") ?: "false").toBoolean()
}

object AccountLinked {
    const val route = "account_linked"
    val uriPattern = "https://mijn.test2.eduid.nl/myconext/api/sp/oidc/redirect"

}

object RequestEduIdLinkSent {
    private const val route = "request_edu_id_link_sent"
    const val emailArg = "email_arg"
    val routeWithArgs = "$route/{$emailArg}"
    val arguments = listOf(navArgument(emailArg) {
        type = NavType.StringType
        nullable = false
        defaultValue = ""
    })

    fun routeWithEmail(email: String) =
        "$route/${URLEncoder.encode(email, Charsets.UTF_8.toString())}"

    fun decodeFromEntry(entry: NavBackStackEntry): String {
        val email = entry.arguments?.getString(emailArg) ?: ""
        return try {
            URLDecoder.decode(email, Charsets.UTF_8.name())
        } catch (e: UnsupportedEncodingException) {
            ""
        }
    }
}

object OAuth {
    private const val route = "oauth_mobile_eduid"
    const val nextStepArg = "confirm_phone_arg"
    const val routeForEnrollment = "$route/enroll"
    const val routeForAuthorization = "$route/authorize"
    const val routeForOAuth = "$route/apiauth"
    const val routeWithArgs = "$route/{$nextStepArg}"
    val arguments = listOf(navArgument(nextStepArg) {
        type = NavType.StringType
        nullable = false
        defaultValue = routeForEnrollment
    })
}

sealed class PhoneNumberRecovery(val route: String) {
    object RequestCode : PhoneNumberRecovery("phone_number_recover")
    object ConfirmCode : PhoneNumberRecovery("phone_number_confirm_code") {
        private const val phoneNumberArg = "phone_number_arg"

        val routeWithArgs = "${route}/{$phoneNumberArg}"
        val arguments = listOf(navArgument(phoneNumberArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        })

        fun routeWithPhoneNumber(phoneNumber: String) =
            "${route}/${URLEncoder.encode(phoneNumber, Charsets.UTF_8.toString())}"

        fun decodeFromEntry(entry: NavBackStackEntry): String {
            val phoneNumberArg = entry.arguments?.getString(phoneNumberArg) ?: ""
            return try {
                URLDecoder.decode(phoneNumberArg, Charsets.UTF_8.name())
            } catch (e: UnsupportedEncodingException) {
                ""
            }
        }

    }
}

sealed class Account(val route: String) {

    object ScanQR : Account("scan")
    object EnrollPinSetup : Account("enroll_pin_setup") {
        const val enrollChallenge = "enroll_challenge_arg"

        val routeWithArgs = "$route/{$enrollChallenge}"
        val arguments = listOf(navArgument(enrollChallenge) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        })
    }

    object Authorize : Account("authorization") {
        const val challengeArg = "challenge_arg"

        val routeWithArgs = "$route/{$challengeArg}"
        val arguments = listOf(navArgument(challengeArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        })
    }

    object DeepLink : Account("deeplinks") {
        const val enrollPattern = "https://eduid.nl/tiqrenroll/"
        const val authPattern = "https://eduid.nl/tiqrauth/"
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