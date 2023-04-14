package nl.eduid.screens.dataactivity

import nl.eduid.ErrorData

data class UiState(
    val data: List<ServiceProvider> = emptyList(),
    val isLoading: Boolean = false,
    val errorData: ErrorData? = null,
    val isComplete: Unit? = null,
    val deleteService: ServiceProvider? = null,
)