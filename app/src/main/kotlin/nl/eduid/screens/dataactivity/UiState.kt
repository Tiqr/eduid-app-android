package nl.eduid.screens.dataactivity

import nl.eduid.ErrorData
import nl.eduid.di.model.TokenResponse

data class UiState(
    val data: List<ServiceProvider> = emptyList(),
    val isLoading: Boolean = false,
    val errorData: ErrorData? = null,
    val deleteService: ServiceProvider? = null,
    val revokeToken: ScopeAccessGrant? = null,
    val promptAuth: Unit? = null,
)