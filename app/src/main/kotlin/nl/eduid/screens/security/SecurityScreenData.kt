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
    val passwordDate: String? = null,
    val passKeys: List<LoginCreatedData> = emptyList(),
    val appCreatedAt: LoginCreatedData? = null,
)

data class LoginCreatedData(val name: String, val createdAt: String)
