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

    const val WELCOME_START = "start"
    const val FIRST_TIME_DIALOG = "first_time_dialog"
    const val PERSONAL_INFO = "personal_info"
    const val DATA_AND_ACTIVITY = "data_and_activity"
    const val SECURITY = "security"
    const val OAUTH = "oauth_mobile_eduid"
    const val RESET_PASSWORD = "reset_password"
    const val RESET_PASSWORD_CONFIRM = "reset_password_confirm"
    const val EDIT_EMAIL = "edit_email"
    const val EDIT_NAME = "edit_name"
    const val TWO_FA_DETAIL = "2fa_detail"
    const val TWO_FA_DELETE = "2fa_delete"
    const val DELETE_ACCOUNT_FIRST_CONFIRM = "delete_account_first_confirm"
    const val DELETE_ACCOUNT_SECOND_CONFIRM = "delete_account_second_confirm"
}

object OAuth {
    const val route = "oauth_mobile_eduid"
}

object RequestEduIdCreated {
    const val route = "request_edu_id_created"
    const val uriPatternHttps = "https://login.test2.eduid.nl/client/mobile/created"
    const val uriPatternCustomScheme = "eduid:///client/mobile/created"
}

object AccountLinked {
    const val route = "account_linked"
    const val uriPatternOK = "https://login.test2.eduid.nl/client/mobile/account-linked"
    const val uriPatternFailed = "https://login.test2.eduid.nl/client/mobile/eppn-already-linked"
    const val uriPatternExpired = "https://login.test2.eduid.nl/client/mobile/expired"
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

    object ScanQR : Account("scan") {
        const val isEnrolment = "is_enrolment"
        val routeWithArgs = "$route/{${isEnrolment}}"
        val routeForEnrol = "$route/true"
        val routeForAuth = "$route/false"
        val arguments = listOf(navArgument(isEnrolment) {
            type = NavType.BoolType
            nullable = false
            defaultValue = true
        })

    }

    object EnrollPinSetup : Account("enroll_pin_setup") {
        const val enrollChallenge = "enroll_challenge_arg"

        val routeWithArgs = "$route/{$enrollChallenge}"
        val arguments = listOf(navArgument(enrollChallenge) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        })
    }

    object RequestAuthentication : Account("authentication") {
        const val challengeArg = "challenge_arg"

        val routeWithArgs = "$route/{$challengeArg}"
        val arguments = listOf(navArgument(challengeArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        })
    }

    object AuthenticationCheckSecret : Account("authentication_checksecret") {
        private const val challengeArg = "challenge_arg"

        val routeWithArgs = "$route/{$challengeArg}"
        val arguments = listOf(navArgument(challengeArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        })
    }

    object AuthenticationCompleted : Account("authentication_completed") {
        private const val challengeArg = "challenge_arg"
        private const val pinArg = "pin_arg"

        val routeWithArgs = "$route/{$challengeArg}?pin={$pinArg}"
        val arguments = listOf(navArgument(challengeArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        }, navArgument(pinArg) {
            type = NavType.StringType
            nullable = true
        })

        fun buildRoute(encodedChallenge: String, pin: String?): String = if (pin.isNullOrEmpty()) {
            "${route}/$encodedChallenge"
        } else {
            "${route}/$encodedChallenge?pin=$pin"
        }

    }

    //https://eduid.nl/tiqrenroll/?metadata=https%3A%2F%2Flogin.test2.eduid.nl%2Ftiqr%2Fmetadata%3Fenrollment_key%3Dd47fa31400084edc043f8c547c5ed3f6b18d69f5a71f422519911f034b865f96153c8fc1507d81bc05aba95d095489a8d0400909f8aab348e2ac1786b28db572
    object DeepLink : Account("deeplinks") {
        const val enrollPattern = "https://eduid.nl/tiqrenroll/?metadata="
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

object ManageAccountRoute {
    private const val route = "manage_account"
    const val dateArg = "date_arg"
    const val routeWithArgs = "$route/{$dateArg}"
    val arguments = listOf(navArgument(dateArg) {
        type = NavType.StringType
        nullable = false
        defaultValue = ""
    })

    fun routeWithArgs(dateString: String) =
        "$route/${URLEncoder.encode(dateString, Charsets.UTF_8.toString())}"


    fun decodeDateFromBundle(bundleArg: String): String {
        return try {
            URLDecoder.decode(bundleArg, Charsets.UTF_8.name())
        } catch (e: UnsupportedEncodingException) {
            ""
        }
    }
}

object DeleteTwoFaRoute {
    private const val route = "delete_two_fa"
    const val idArg = "id_arg"
    const val routeWithArgs = "$route/{$idArg}"
    val arguments = listOf(navArgument(idArg) {
        type = NavType.StringType
        nullable = false
        defaultValue = ""
    })

    fun routeWithArgs(idString: String) =
        "$route/${URLEncoder.encode(idString, Charsets.UTF_8.toString())}"

    fun decodeIdFromEntry(entry: NavBackStackEntry): String {
        val date = entry.arguments?.getString(idArg) ?: ""
        return try {
            URLDecoder.decode(date, Charsets.UTF_8.name())
        } catch (e: UnsupportedEncodingException) {
            ""
        }
    }
}