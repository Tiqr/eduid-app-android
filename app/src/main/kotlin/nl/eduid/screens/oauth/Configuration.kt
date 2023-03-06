package nl.eduid.screens.oauth

import android.net.Uri
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Configuration(
    @Json(name = "client_id")
    val clientId: String?,
    @Json(name = "authorization_scope")
    val scope: String,
    @Json(name = "redirect_uri")
    val redirect: String,
    @Json(name = "end_session_redirect_uri")
    val endSessionRedirect: String,
    @Json(name = "discovery_uri")
    val discovery: String?,
    @Json(name = "authorization_endpoint_uri")
    val authEndpoint: String,
    @Json(name = "token_endpoint_uri")
    val tokenEndpoint: String,
    @Json(name = "user_info_endpoint_uri")
    val endSessionEndpoint: String,
    @Json(name = "registration_endpoint_uri")
    val registrationEndpoint: String,
    @Json(name = "https_required")
    val isHttpsRequired: Boolean
) : Parcelable {
    @IgnoredOnParcel
    val redirectUri: Uri = Uri.parse(redirect)

    @IgnoredOnParcel
    val endSessionRedirectUri: Uri = Uri.parse(endSessionRedirect)

    @IgnoredOnParcel
    val discoveryUri: Uri? = discovery?.let { Uri.parse(it) }

    @IgnoredOnParcel
    val authEndpointUri: Uri = Uri.parse(authEndpoint)

    @IgnoredOnParcel
    val tokenEndpointUri: Uri = Uri.parse(tokenEndpoint)

    @IgnoredOnParcel
    val endSessionEndpointUri: Uri = Uri.parse(endSessionEndpoint)

    @IgnoredOnParcel
    val registrationEndpointUri: Uri = Uri.parse(registrationEndpoint)

    companion object {
        val EMPTY = Configuration(
            clientId = "",
            scope = "",
            redirect = "",
            endSessionRedirect = "",
            discovery = "",
            authEndpoint = "",
            tokenEndpoint = "",
            endSessionEndpoint = "",
            registrationEndpoint = "",
            isHttpsRequired = true

        )
    }
}