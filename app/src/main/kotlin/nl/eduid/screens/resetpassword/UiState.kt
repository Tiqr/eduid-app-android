package nl.eduid.screens.resetpassword

import nl.eduid.ErrorData

data class UiState(
    val password: Password = Password.Add,
    val emailUsed: String = "",
    val inProgress: Boolean = false,
    val errorData: ErrorData? = null,
    val isCompleted: Unit? = null,
)

sealed class Password {
    object Add : Password()
    object Change : Password()
}