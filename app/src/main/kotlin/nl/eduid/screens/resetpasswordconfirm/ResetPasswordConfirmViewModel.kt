package nl.eduid.screens.resetpasswordconfirm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.api.EduIdApi
import nl.eduid.di.model.UpdatePasswordRequest
import nl.eduid.graphs.ConfigurePassword
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ResetPasswordConfirmViewModel @Inject constructor(
    private val eduIdApi: EduIdApi,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val passwordHash: String
    var uiState by mutableStateOf(UiState())
        private set

    init {
        passwordHash = savedStateHandle.get<String>(ConfigurePassword.Form.passwordHashArg) ?: ""
    }

    fun onNewPasswordInput(newValue: String) {
        uiState = uiState.copy(newPasswordInput = newValue)
    }

    fun onConfirmPasswordInput(newValue: String) {
        uiState = uiState.copy(confirmPasswordInput = newValue)
    }

    fun dismissError() {
        uiState = uiState.copy(errorData = null, isCompleted = null)
    }


    fun onResetPasswordClicked() {
        if (uiState.passwordIsValid() && passwordHash.isNotEmpty()) {
            uiState = uiState.copy(inProgress = true)
            changePassword(uiState, newPassword = uiState.newPasswordInput)
        } else {
            val detailMessage = if (uiState.newPasswordInput.isNullOrEmpty()) {
                R.string.err_msg_pass_is_invalid
            } else if (uiState.newPasswordInput != uiState.confirmPasswordInput) {
                R.string.err_msg_pass_do_not_match
            } else {
                R.string.err_msg_pass_missing_hash
            }
            uiState = uiState.copy(
                errorData = ErrorData(
                    titleId = R.string.err_title_cannot_update_pass,
                    messageId = detailMessage
                )
            )
        }
    }

    fun onDeletePasswordClicked() {
        uiState = uiState.copy(inProgress = true)
        changePassword(uiState, newPassword = "")
    }

    fun clearCompleted() {
        uiState = uiState.copy(isCompleted = null)
    }

    private fun changePassword(currentState: UiState, newPassword: String) = viewModelScope.launch {
        try {
            val hashIsValidResponse = eduIdApi.checkHashIsValid(passwordHash)
            if (hashIsValidResponse.isSuccessful && hashIsValidResponse.body() == true) {
                val response = eduIdApi.updatePassword(
                    UpdatePasswordRequest(newPassword = newPassword, hash = passwordHash)
                )
                if (response.isSuccessful) {
                    uiState = currentState.copy(
                        inProgress = false, isCompleted = Unit, errorData = null
                    )
                } else {
                    uiState = currentState.copy(
                        inProgress = false, isCompleted = null, errorData = ErrorData(
                            titleId = R.string.err_title_cannot_update_pass,
                            messageId = R.string.err_msg_auth_unexpected_arg,
                            messageArg = "[${response.code()}/${response.message()}]${
                                response.errorBody()?.string()
                            }",
                        )
                    )
                }
            } else {
                uiState = currentState.copy(
                    inProgress = false, isCompleted = null, errorData = ErrorData(
                        titleId = R.string.err_title_cannot_update_pass,
                        messageId = R.string.err_msg_pass_invalid_hash,
                    )
                )
            }
        } catch (e: Exception) {
            uiState = currentState.copy(
                inProgress = false, errorData = ErrorData(
                    titleId = R.string.err_title_cannot_update_pass,
                    messageId = R.string.err_msg_auth_unexpected_arg,
                    messageArg = e.message ?: e.javaClass.simpleName,
                )
            )
            Timber.e(e, "Failed to update password")
        }
    }
}