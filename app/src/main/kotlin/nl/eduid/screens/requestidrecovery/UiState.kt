package nl.eduid.screens.requestidrecovery

import nl.eduid.ErrorData

data class UiState(
    val input: String = "",
    val inProgress: Boolean = false,
    val errorData: ErrorData? = null
)