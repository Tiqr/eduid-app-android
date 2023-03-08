package nl.eduid.di.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


data class RequestNewIdRequest(
    val user: User,
    val authenticationRequestId: String,
) {
    data class User(
        val email: String,
        val givenName: String,
        val familyName: String,
    )
}

//{"user":{"email":"test4@test.com","givenName":"Tester","familyName":"Testerson"},"authenticationRequestId":"48e0eb5f-62ae-429e-b103-444ad24f2cc0"}


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
//    val registration: JsonObject
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EduIdPerServiceProvider(
    val serviceProviderEntityId: String,
    val value: String,
    val serviceName: String,
    val serviceNameNl: String,
    val serviceLogoUrl: String,
    val createdAt: Long
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class LinkedAccount(
    val institutionIdentifier: String,
    val schacHomeOrganization: String,
    val eduPersonPrincipalName: String,
    val givenName: String,
    val familyName: String,
    val eduPersonAffiliations: List<String>,
    val createdAt: Long,
    val expiresAt: Long
) : Parcelable