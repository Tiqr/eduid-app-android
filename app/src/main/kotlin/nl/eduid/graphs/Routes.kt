package nl.eduid.graphs

import android.net.Uri
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import nl.eduid.di.model.ControlCode
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.graphs.AccountLinked.isRegistrationFlowArg
import nl.eduid.graphs.VerifyIdentityRoute.isLinkedAccount
import nl.eduid.screens.emailcodeentry.EmailCodeEntryViewModel
import org.tiqr.data.model.AuthenticationChallenge
import timber.log.Timber
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
    const val overrideUrlArg = "override_url"
    const val routeWithArgs = "${route}?overrideUrl={$overrideUrlArg}"
    val arguments = listOf(navArgument(overrideUrlArg) {
        type = NavType.StringType
        nullable = true
    })

    fun routeWithOverrideUrl(overrideUrl: String?): String {
        return if (overrideUrl.isNullOrEmpty()) {
            route
        } else {
            "$route?overrideUrl=${Uri.encode(overrideUrl)}"
        }
    }
}

object RequestEduIdCreated {
    const val route = "request_edu_id_created"
    fun getUriPatternHttps(baseUrl: String) = "$baseUrl/client/mobile/created"
    val customScheme = "eduid:///client/mobile/created"

    /**
     * After the account is created, the server redirects with the production URL, not the environment dependent URL. The production
     * `.well-known/assetlinks.json` does not include the signature from the testing variant We do not have the production signing, so we
     * must match both environment dependent link *and* production environment redirect link.
     */
    val uriProdPatternHttps = "https://login.eduid.nl/client/mobile/created"
}

object AccountLinked {
    const val route = "account_linked"
    fun getUriPatternInternalLinkOK(baseUrl: String) = "$baseUrl/client/mobile/account-linked"
    fun getUriPatternExternalLinkOK(baseUrl: String) = "$baseUrl/client/mobile/external-account-linked"
    fun getUriPatternExternalLinkOKCustomScheme() = "eduid:///client/mobile/external-account-linked"
    fun getUriPatternFailed(baseUrl: String) = "$baseUrl/client/mobile/eppn-already-linked"
    fun getUriPatternExpired(baseUrl: String) = "$baseUrl/client/mobile/expired"
    fun getUriPatternSubjectAlreadyLinked(baseUrl: String) = "$baseUrl/client/mobile/verify-already-used"
    fun getUriPatternSubjectAlreadyLinkedCustomScheme() = "eduid:///client/mobile/verify-already-used"

    const val isRegistrationFlowArg = "is_registration_flow"

    const val routeWithArgs = "${route}?isRegistrationFlow={$isRegistrationFlowArg}"
    val arguments = listOf(navArgument(isRegistrationFlowArg) {
        type = NavType.BoolType
        nullable = false
        defaultValue = false
    })

    fun routeWithRegistrationFlowParam(isRegistrationFlow: Boolean) =
        "$route/isRegistrationFlow=$isRegistrationFlow"

}

object VerifiedPersonalInfoRoute {
    const val route = "verified_personal_info"
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

    object OneTimePassword : Account("one_time_password") {
        private const val challengeArg = "challenge_arg"
        const val pinArg = "pin_arg"

        val routeWithArgs = "$route/{$challengeArg}/{$pinArg}"
        val arguments = listOf(navArgument(challengeArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        }, navArgument(pinArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        })

        fun buildRoute(encodedChallenge: String, pin: String): String = "${route}/$encodedChallenge/$pin"
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

        fun getConfirmEmail(baseUrl: String) =
            "$baseUrl/client/mobile/update-email?$confirmEmailHash={$confirmEmailHash}"
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
            "${route}/${Uri.encode(selfAssertedName.chosenName ?: "")}/${Uri.encode(selfAssertedName.familyName ?: "")}/${canEditFamilyName}"
    }
}


object EmailCodeEntry {
    private const val route = "email_code_entry"
    const val emailArg = "email"
    const val codeHashArg = "code_hash"
    const val codeContextArg = "code_context"
    const val routeWithArgs = "$route/{$codeContextArg}/{$emailArg}?{$codeHashArg}"

    val arguments = listOf(navArgument(emailArg) {
        type = NavType.StringType
        nullable = false
        defaultValue = ""
    }, navArgument(codeHashArg) {
        type = NavType.StringType
        nullable = true
    }, navArgument(codeContextArg) {
        type = NavType.StringType
        nullable = false
        defaultValue = EmailCodeEntryViewModel.CodeContext.Registration.name
    })

    fun routeWithArgs(email: String, codeHash: String?, codeContext: EmailCodeEntryViewModel.CodeContext) = "$route/${codeContext.name}/$email?$codeHash"
}



object VerifyIdentityRoute {
    private const val route = "verify_identity"
    const val isLinkedAccount = "is_linked_account"
    const val routeWithArgs = "$route?$isLinkedAccount={$isLinkedAccount}"

    val arguments = listOf(navArgument(isLinkedAccount) {
        type = NavType.BoolType
        nullable = false
        defaultValue = false
    })

    fun routeWithArgs(isLinkedAccount: Boolean) = "$route?${VerifyIdentityRoute.isLinkedAccount}=$isLinkedAccount"
}

object SelectYourBankRoute {
    const val route = "select_your_bank"
}

object VerifyIdentityWithIdIntro {
    const val route = "verify_identity_with_id_intro"
}

object VerifyIdentityWithIdInput {
    const val route = "verify_identity_with_id_input"

    const val codeArg = "code"
    const val routeWithArgs = "${route}?$codeArg={$codeArg}"

    val arguments = listOf(navArgument(codeArg) {
        type = NavType.StringType
        nullable = true
    })

    fun routeWithArgs(code: ControlCode): String {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(ControlCode::class.java)
        val jsonCode = adapter.toJson(code)
        val encodedCode = Uri.encode(jsonCode)
        return "$route?$codeArg=$encodedCode"
    }

    fun deserializeCode(encodedCode: String?): ControlCode? {
        if (encodedCode == null) {
            return null
        }
        try {
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(ControlCode::class.java)
            val jsonCode = Uri.decode(encodedCode)
            return adapter.fromJson(jsonCode)
        } catch (ex: Exception) {
            Timber.w(ex, "Could not decode ControlCode!")
            return null
        }
    }
}

object VerifyIdentityWithIdCode {
    const val route = "verify_identity_with_id_code"

    const val codeArg = "code"
    const val routeWithArgs = "${route}?$codeArg={$codeArg}"

    val arguments = listOf(navArgument(codeArg) {
        type = NavType.StringType
        nullable = false
    })

    fun routeWithArgs(code: ControlCode): String {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(ControlCode::class.java)
        val jsonCode = adapter.toJson(code)
        val encodedCode = Uri.encode(jsonCode)
        return "${route}?${codeArg}=$encodedCode"
    }

    fun deserializeCode(encodedCode: String?): ControlCode? {
        try {
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(ControlCode::class.java)
            val jsonCode = Uri.decode(encodedCode)
            return adapter.fromJson(jsonCode)
        } catch (ex: Exception) {
            Timber.w(ex, "Could not decode ControlCode!")
            return null
        }
    }
}

object ExternalAccountLinkedError {
    const val route = "external_account_linked_error"
    fun getUriPattern(baseUrl: String) = "$baseUrl/client/mobile/external-account-linked-error"
}
