package nl.eduid.screens.dataactivity

import nl.eduid.ErrorData

data class UiState(
    val data: DataAndActivityData = DataAndActivityData(),
    val isLoading: Boolean = false,
    val errorData: ErrorData? = null,
    val isComplete: Unit? = null,
)