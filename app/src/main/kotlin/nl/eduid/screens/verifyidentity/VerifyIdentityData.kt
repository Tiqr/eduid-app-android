package nl.eduid.screens.verifyidentity

import android.content.Intent
import nl.eduid.ErrorData

data class VerifyIdentityData(
    val isLoading: Boolean = false,
    val errorData: ErrorData? = null,
    val launchIntent: Intent? = null
)
