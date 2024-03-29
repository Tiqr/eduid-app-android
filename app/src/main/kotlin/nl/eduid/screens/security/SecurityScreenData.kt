package nl.eduid.screens.security

import nl.eduid.ErrorData

data class SecurityScreenData(
    val isLoading: Boolean = false,
    val errorData: ErrorData? = null,
    val twoFAProvider: String? = null,
    val email: String = "",
    val hasPassword: Boolean = false,
)