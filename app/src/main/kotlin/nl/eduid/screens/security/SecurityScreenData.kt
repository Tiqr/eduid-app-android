package nl.eduid.screens.security

import nl.eduid.ErrorData
import nl.eduid.di.model.PublicKeyCredential

data class SecurityScreenData(
    val isLoading: Boolean = false,
    val errorData: ErrorData? = null,
    val twoFAProvider: String? = null,
    val lastChangedPassword: String? = null,
    val email: String = "",
    val showAddSecurityKey: Boolean = false,

    val hasPassword: Boolean = false,
    val passwordDate: String? = null,
    val passKeys: List<PasskeyData> = emptyList(),
    val appCreatedAt: LoginCreatedData? = null,
    val showRecoveryOptions: Boolean = false,
    val withPhoneNumber: String? = null,
    val withRecoveryCode: Boolean = false,
)

data class LoginCreatedData(val name: String, val createdAt: String)

data class PasskeyData(val name: String, val createdAt: String, val credential: PublicKeyCredential)
