package nl.eduid.screens.twofactorkey

import nl.eduid.ErrorData

data class TwoFactorData(
    val isLoading: Boolean = false,
    val keys: List<IdentityData> = emptyList(),
    val errorData: ErrorData? = null
)

data class IdentityData(
    val uniqueKey: String = "",
    val title: String = "",
    val subtitle: String = "",
    val account: String = "",
    val biometricFlag: Boolean = false,
    val isExpanded: Boolean = false,
)

