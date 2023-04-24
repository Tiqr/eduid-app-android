package nl.eduid.screens.security

data class SecurityScreenData(
    val twoFactorEnabled: Boolean = false,
    val email: String = "",
    val hasPassword: Boolean = false,
)