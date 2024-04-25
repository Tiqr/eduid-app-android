package nl.eduid.graphs

import android.net.Uri
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.env.EnvironmentProvider
import java.io.UnsupportedEncodingException

object Graph {
    const val HOME_PAGE = "home_page"
    const val REQUEST_EDU_ID_ACCOUNT = "request_edu_id_account"
    const val REQUEST_EDU_ID_FORM = "request_edu_id_details"

    const val WELCOME_START = "start"
    const val FIRST_TIME_DIALOG = "first_time_dialog"
    const val CONTINUE_RECOVERY_IN_BROWSER = "continue_recovery_in_browser"
    const val PERSONAL_INFO = "personal_info"
    const val DATA_AND_ACTIVITY = "data_and_activity"
    const val OAUTH = "oauth_mobile_eduid"
    const val CONFIGURE_PASSWORD = "configure_password_subgraph"
    const val EDIT_EMAIL = "edit_email"
    const val TWO_FA_DETAIL = "2fa_detail"
    const val DELETE_ACCOUNT_FIRST_CONFIRM = "delete_account_first_confirm"
    const val DELETE_ACCOUNT_SECOND_CONFIRM = "delete_account_second_confirm"
    const val AUTH_GRAPH = "auth_nestedgraph"
}

object OAuth {
    const val route = "oauth_mobile_eduid"
}

object RequestEduIdCreated {
    const val route = "request_edu_id_created"
    val uriPatternHttps = "${EnvironmentProvider.getCurrent().baseUrl}/client/mobile/created"
    val customScheme = "eduid:///client/mobile/created"

    /**
     * After the account is created, the server redirects with the production URL, not the environment
     * dependent URL.  The production `.well-known/assetlinks.json` does not include the signature from the testing variant
     * We do not have the production signing, so we must match both environment dependent link *and* production
     * environment redirect link.
     * */
    val uriProdPatternHttps = "https://login.eduid.nl/client/mobile/created"
}

object AccountLinked {
    const val route = "account_linked"
    val uriPatternOK = "${EnvironmentProvider.getCurrent().baseUrl}/client/mobile/account-linked"
    val uriPatternFailed =
        "${EnvironmentProvider.getCurrent().baseUrl}/client/mobile/eppn-already-linked"
    val uriPatternExpired = "${EnvironmentProvider.getCurrent().baseUrl}/client/mobile/expired"
}

object RequestEduIdLinkSent {
    private const val route = "request_edu_id_link_sent"
    const val LOGIN_REASON = "magiclink_for_login"
    const val ADD_PASSWORD_REASON = "magiclink_for_add_password"
    const val CHANGE_PASSWORD_REASON = "magiclink_for_change_password"
    private const val emailArg = "email_arg"
    const val reasonArg = "reason_arg"
    const val routeWithArgs = "$route/{$emailArg}/{$reasonArg}"
    val arguments = listOf(navArgument(emailArg) {
        type = NavType.StringType
        nullable = false
        defaultValue = ""
    }, navArgument(reasonArg) {
        type = NavType.StringType
        nullable = false
        defaultValue = LOGIN_REASON
    })

    fun routeWithEmail(email: String, reason: String = LOGIN_REASON) =
        "$route/${Uri.encode(email)}/$reason"

    fun decodeEmailFromEntry(entry: NavBackStackEntry): String {
        val email = entry.arguments?.getString(emailArg) ?: ""
        return Uri.decode(email)
    }
}

sealed class PhoneNumberRecovery(val route: String) {
    data object RequestCode : PhoneNumberRecovery("phone_number_recover")
    data object ConfirmCode : PhoneNumberRecovery("phone_number_confirm_code") {
        private const val phoneNumberArg = "phone_number_arg"
        const val isDeactivationArg = "is_deactivation_arg"
        val routeWithArgs = "${route}/{$phoneNumberArg}/{$isDeactivationArg}"
        val arguments = listOf(navArgument(phoneNumberArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        }, navArgument(isDeactivationArg) {
            type = NavType.BoolType
            nullable = false
            defaultValue = false
        })

        fun routeWithPhoneNumber(phoneNumber: String, isDeactivation: Boolean = false) =
            "${route}/${Uri.encode(phoneNumber)}/$isDeactivation"

        fun decodeFromEntry(entry: NavBackStackEntry): String {
            val phoneNumberArg = entry.arguments?.getString(phoneNumberArg) ?: ""
            return Uri.decode(phoneNumberArg)
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

        @SuppressWarnings("unused")
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

    fun routeWithArgs(dateString: String) = "$route/${Uri.encode(dateString)}"


    fun decodeDateFromBundle(bundleArg: String): String {
        return try {
            Uri.decode(bundleArg)
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

    fun routeWithArgs(idString: String) = "$route/${Uri.encode(idString)}"

    fun decodeIdFromEntry(entry: NavBackStackEntry): String {
        val date = entry.arguments?.getString(idArg) ?: ""
        return Uri.decode(date)
    }
}

sealed class Security(val route: String) {
    object Settings : Security("security")

    object ConfirmEmail : Security("confirm_email") {
        const val confirmEmailHash = "h"
        val routeWithArgs = "$route?$confirmEmailHash={$confirmEmailHash}"
        val arguments = listOf(navArgument(confirmEmailHash) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        })
        val confirmEmail =
            "${EnvironmentProvider.getCurrent().baseUrl}/client/mobile/update-email?$confirmEmailHash={$confirmEmailHash}"
    }
}

sealed class EditName(val route: String) {

    object Form : EditName("edit_name_form") {
        const val chosenName = "chosenName"
        const val familyName = "familyName"
        const val canEditFamilyName = "canEditFamilyname"
        val routeWithArgs = "${route}/{$chosenName}/{$familyName}/{$canEditFamilyName}"
        val arguments = listOf(navArgument(chosenName) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        }, navArgument(familyName) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        }, navArgument(canEditFamilyName) {
            type = NavType.BoolType
            nullable = false
            defaultValue = true
        })

        fun routeWithArgs(selfAssertedName: SelfAssertedName, canEditFamilyName: Boolean) =
            "${route}/${Uri.encode(selfAssertedName.chosenName)}/${Uri.encode(selfAssertedName.familyName)}/${canEditFamilyName}"

        fun decodeIdFromEntry(entry: NavBackStackEntry): SelfAssertedName {
            val choseName = entry.arguments?.getString(chosenName) ?: ""
            val familyName = entry.arguments?.getString(familyName) ?: ""
            return SelfAssertedName(
                familyName = Uri.decode(familyName), chosenName = Uri.decode(choseName)
            )
        }
    }
}