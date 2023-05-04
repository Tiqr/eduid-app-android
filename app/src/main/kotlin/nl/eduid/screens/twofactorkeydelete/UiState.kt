package nl.eduid.screens.twofactorkeydelete

import nl.eduid.ErrorData

data class UiState(
    val inProgress: Boolean = false,
    val errorData: ErrorData? = null,
    val isCompleted: Unit? = null,
) {
}