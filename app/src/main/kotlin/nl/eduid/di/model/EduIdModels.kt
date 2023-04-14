package nl.eduid.di.model

import android.os.Parcelable
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
    val familyName: String,
    val usePassword: Boolean,
    val usePublicKey: Boolean,
    val forgottenPassword: Boolean,
//    val publicKeyCredentials: List<Any?>,
    val linkedAccounts: List<LinkedAccount>,
    val schacHomeOrganization: String,
    val uid: String,
    val rememberMe: Boolean,
    val created: Long,

    val eduIdPerServiceProvider: Map<String, EduIdPerServiceProvider>,

    val loginOptions: List<String>,
    val registration: Registration?,
) : Parcelable {

    fun isRecoveryRequired(): Boolean = registration?.status != "FINALIZED"
}

@Parcelize
@JsonClass(generateAdapter = true)
data class EduIdPerServiceProvider(
    val serviceProviderEntityId: String,
    val value: String,
    val serviceName: String,
    val serviceNameNl: String,
    val serviceLogoUrl: String,
    val createdAt: Long,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class LinkedAccount(
    val institutionIdentifier: String,
    val schacHomeOrganization: String,
    val eduPersonPrincipalName: String,
    val subjectId: String?,
    val givenName: String,
    val familyName: String,
    val eduPersonAffiliations: List<String>,
    val createdAt: Long,
    val expiresAt: Long,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Registration(
    val phoneNumber: String?,
    val phoneVerified: Boolean?,
    val recoveryCode: Boolean?,
    //FINALIZED - is completed
    val status: String?,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class InstitutionNameResponse(
    val displayNameEn: String,
    val displayNameNl: String,

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
data class Token(
    val id: String,
    val type: String,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class TokenResponse(
    val id: String,
    val clientName: String,
    val clientId: String,
    val type: String,
    val scopes: List<Scope>?,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Scope(val name: String, val descriptions: Description?) : Parcelable {
    fun hasValidDescription(): Boolean =
        descriptions != null && (descriptions.en != null || descriptions.nl != null)
}

@Parcelize
@JsonClass(generateAdapter = true)
data class Description(val en: String?, val nl: String?) : Parcelable