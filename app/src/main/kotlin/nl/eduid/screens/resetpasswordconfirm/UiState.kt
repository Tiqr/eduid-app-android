package nl.eduid.screens.resetpasswordconfirm

import nl.eduid.ErrorData

data class UiState(
    val newPasswordInput: String = "",
    val confirmPasswordInput: String = "",
    val inProgress: Boolean = false,
    val errorData: ErrorData? = null,
    val isCompleted: Unit? = null,
) {
    fun passwordIsValid() =
        newPasswordInput.isNotEmpty() && newPasswordInput == confirmPasswordInput
}
