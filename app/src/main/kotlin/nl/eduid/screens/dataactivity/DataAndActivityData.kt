package nl.eduid.screens.dataactivity

import androidx.compose.runtime.Stable
import nl.eduid.di.model.TokenResponse

@Stable
data class ServiceProvider(
    val providerName: String?,
    val createdStamp: Long?,
    val firstLoginStamp: Long?,
    val uniqueId: String?,
    val scopeAccessGrant: ScopeAccessGrant? = null,
    val serviceProviderEntityId: String,
    val providerLogoUrl: String? = null,
) {
    val hasDataAccess: Boolean = scopeAccessGrant != null
}

@Stable
data class ScopeAccessGrant(
    val clientId: String,
    val forProviderName: String?,
    val token: TokenResponse? = null,
    val scopeDescription: String? = null,
    val grantedOn: String?,
    val expireAt: String?,
)