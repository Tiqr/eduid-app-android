package nl.eduid.screens.deleteaccountsecondconfirm

import nl.eduid.ErrorData

data class UiState(
    val fullName: String = "",
    val inProgress: Boolean = false,
    val isDeleted: Unit? = null,
    val errorData: ErrorData? = null,
)