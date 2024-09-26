package nl.eduid.screens.security

import nl.eduid.ErrorData

data class SecurityScreenData(
    val isLoading: Boolean = false,
    val errorData: ErrorData? = null,
    val twoFAProvider: String? = null,
    val lastChangedPassword: String? = null,
    val email: String = "",
    val showAddSecurityKey: Boolean = false,

    val hasPassword: Boolean = false,
)