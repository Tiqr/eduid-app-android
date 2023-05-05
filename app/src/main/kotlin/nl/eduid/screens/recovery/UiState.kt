package nl.eduid.screens.recovery

import nl.eduid.ErrorData

data class UiState(
    val input: String = "",
    val inProgress: Boolean = false,
    val errorData: ErrorData? = null,
    val isCompleted: Unit? = null,
)