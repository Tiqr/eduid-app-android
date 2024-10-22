package nl.eduid.screens.verifyidentity

import nl.eduid.ErrorData

data class VerifyIdentityData(
    val isLoading: Boolean = false,
    val errorData: ErrorData? = null
)
