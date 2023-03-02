package nl.eduid.screens.oauth

import nl.eduid.screens.scan.ErrorData

/**
 * Loading
 * Initialized
 * Authorizing && Launched <- from screen
 * ExchangingTokenRequest && launched = false
 * Authorized
 * */
sealed class OAuthStep() {
    object Loading : OAuthStep()
    object Initialized : OAuthStep()
    object ExchangingTokenRequest : OAuthStep()
    object Authorized : OAuthStep()
    object Error : OAuthStep()

    val isProcessing: Boolean
        get() = this is Loading || this is ExchangingTokenRequest
}

data class UiState(
    val oauthStep: OAuthStep,
    val error: ErrorData? = null,
    val promptBiometric: Boolean? = null
)

