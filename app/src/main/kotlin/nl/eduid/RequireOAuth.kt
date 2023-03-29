package nl.eduid

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Encapsulated the state for an ongoing OAuth flow that needs to happen before making API calls that
 * require the access token
 * */
@Parcelize
data class RequireOAuth(val isProcessing: Boolean = false, val isOAuthOngoing: Boolean = false) :
    Parcelable
