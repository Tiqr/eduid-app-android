package nl.eduid.screens.resetpassword

import nl.eduid.ErrorData

data class UiState(
    val inProgress: Boolean = false,
    val errorData: ErrorData? = null,
    val requestCompleted: Unit? = null,
)