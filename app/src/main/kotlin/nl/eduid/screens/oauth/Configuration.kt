package nl.eduid.screens.oauth

import android.net.Uri

data class Configuration(
    val clientId: String?,
    val scope: String,
    val redirectUri: Uri,
    val endSessionRedirectUri: Uri,
    val discoveryUri: Uri?,
    val authEndpointUri: Uri,
    val tokenEndpointUri: Uri,
    val endSessionEndpoint: Uri,
    val registrationEndpointUri: Uri,
    val isHttpsRequired: Boolean
) {
    companion object {
        val EMPTY = Configuration(
            clientId = "",
            scope = "",
            redirectUri = Uri.EMPTY,
            endSessionRedirectUri = Uri.EMPTY,
            discoveryUri = Uri.EMPTY,
            authEndpointUri = Uri.EMPTY,
            tokenEndpointUri = Uri.EMPTY,
            endSessionEndpoint = Uri.EMPTY,
            registrationEndpointUri = Uri.EMPTY,
            isHttpsRequired = true

        )
    }
}