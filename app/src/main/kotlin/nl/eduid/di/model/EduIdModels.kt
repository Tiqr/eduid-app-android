package nl.eduid.di.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class RequestEduIdAccount(
    val email: String,
    val givenName: String,
    val familyName: String,
    val relyingPartClientId: String,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class RequestPhoneCode(
    val phoneNumber: String,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ConfirmPhoneCode(
    val phoneVerification: String,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ConfirmDeactivationCode(
    val verificationCode: String,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class UrlResponse(
    val url: String,
) : Parcelable

const val CREATE_EMAIL_SENT = 201
const val FAIL_EMAIL_IN_USE = 409
const val EMAIL_DOMAIN_FORBIDDEN = 412

@Parcelize
@JsonClass(generateAdapter = true)
data class EnrollResponse(
    val url: String,
    val enrollmentKey: String,
    @Json(name = "qrcode") val qrCode: String,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class UserDetails(
    val id: String,
    val email: String,
    val givenName: String,
    val chosenName: String,
    val familyName: String,
    val dateOfBirth: Long?,
    val usePassword: Boolean,
    val usePublicKey: Boolean,
    val forgottenPassword: Boolean,
    val controlCode: ControlCode?,
    val linkedAccounts: List<LinkedAccount>,
    val externalLinkedAccounts: List<ExternalLinkedAccount>,
    val schacHomeOrganization: String,
    val uid: String,
    val rememberMe: Boolean,
    val created: Long,
    val eduIdPerServiceProvider: Map<String, EduIdPerServiceProvider>,
    val loginOptions: List<String>,
    val registration: Registration?,
) : Parcelable {
    fun isRecoveryRequired(): Boolean = registration?.status != "FINALIZED"

    fun hasPasswordSet(): Boolean = loginOptions.contains("usePassword")

    fun hasAppRegistered(): Boolean = loginOptions.contains("useApp")
}

@Parcelize
@JsonClass(generateAdapter = true)
data class EduIdPerServiceProvider(
    val serviceProviderEntityId: String,
    val value: String?,
    val serviceName: String?,
    val serviceNameNl: String?,
    val serviceLogoUrl: String?,
    val createdAt: Long?,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class LinkedAccount(
    val institutionIdentifier: String,
    val schacHomeOrganization: String,
    val eduPersonPrincipalName: String?,
    val subjectId: String?,
    val givenName: String?,
    val familyName: String?,
    val eduPersonAffiliations: List<String>,
    val createdAt: Long,
    val expiresAt: Long,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class LinkedAccountUpdateRequest(
    val eduPersonPrincipalName: String?,
    val subjectId: String?,
    val external: Boolean,
    val idpScoping: String?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ExternalLinkedAccount(
    val idpScoping: String?,
    val subjectId: String?,
    val issuer: ExternalLinkedAccountIssuer?,
    val subjectIssuer: String?,
    val firstName: String?,
    val preferredLastName: String?,
    val legalLastName: String?,
    val familyName: String?,
    val givenName: String?,
    val dateOfBirth: Long?,
    val createdAt: Long,
    val expiresAt: Long
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ExternalLinkedAccountIssuer(
    val id: String,
    val name: String?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Registration(
    val phoneNumber: String?,
    val phoneVerified: Boolean?,
    val recoveryCode: Boolean?,
    // FINALIZED - is completed
    val status: String?,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class InstitutionNameResponse(
    val displayNameEn: String?,
    val displayNameNl: String?,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EmailChangeRequest(
    val email: String,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DeleteServiceRequest(
    val serviceProviderEntityId: String,
    val tokens: List<Token>,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DeleteTokensRequest(
    val tokens: List<Token>,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Token(
    val id: String,
    val type: String,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class TokenResponse(
    val id: String,
    val expiresIn: String,
    val createdAt: String,
    val clientName: String,
    val clientId: String,
    val type: String,
    val scopes: List<Scope>?,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Scope(
    val name: String,
    val descriptions: Description?,
) : Parcelable {
    fun hasValidDescription(): Boolean = descriptions != null && (descriptions.en != null || descriptions.nl != null)
}

@Parcelize
@JsonClass(generateAdapter = true)
data class Description(
    val en: String?,
    val nl: String?,
) : Parcelable

@Stable
@Parcelize
@JsonClass(generateAdapter = true)
data class SelfAssertedName(
    val familyName: String? = null,
    val givenName: String? = null,
    val chosenName: String? = null,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ConfirmedName(
    val familyName: String? = null,
    val familyNameConfirmedBy: String? = null,
    val givenName: String? = null,
    val givenNameConfirmedBy: String? = null,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class UpdatePasswordRequest(
    val newPassword: String,
    val hash: String,
) : Parcelable

enum class IdpScoping {
    EHERKENNING,
    IDIN,
    STUDIELINK;

    /**
     * @Json doesn't work on enum values for serialization, so we override toString instead
     */
    override fun toString(): String {
        return when (this) {
            EHERKENNING -> "eherkenning"
            IDIN -> "idin"
            STUDIELINK -> "studielink"
        }
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class VerifyIssuer(
    val id: String?,
    val name: String?,
    val logo: String?
) : Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class ControlCodeRequest(
    val firstName: String,
    val lastName: String,
    val dayOfBirth: String
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ControlCode(
    val firstName: String?,
    val lastName: String?,
    val dayOfBirth: String?,
    val code: String,
) : Parcelable