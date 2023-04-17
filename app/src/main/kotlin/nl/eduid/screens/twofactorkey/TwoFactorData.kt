package nl.eduid.screens.twofactorkey

data class TwoFactorData(
    val uniqueKey: String = "",
    val title: String = "",
    val subtitle: String = "",
    val account: String = "",
    val providerLogoUrl: String = "",
    val biometricFlag: Boolean = false,
)