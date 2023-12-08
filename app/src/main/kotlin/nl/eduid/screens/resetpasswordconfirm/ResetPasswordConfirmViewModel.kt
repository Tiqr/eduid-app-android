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
                R.string.ResponseErrors_InvalidPasswordError_COPY
            } else if (uiState.newPasswordInput != uiState.confirmPasswordInput) {
                R.string.ResponseErrors_PasswordMismatchError_COPY
            } else {
                R.string.ResponseErrors_MissingPasswordHash_COPY
            }
            uiState = uiState.copy(
                errorData = ErrorData(
                    titleId = R.string.ResponseErrors_PasswordUpdateError_COPY,
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
                            titleId = R.string.ResponseErrors_PasswordUpdateError_COPY,
                            messageId = R.string.Generic_RequestError_Description_COPY,
                            messageArg = "[${response.code()}/${response.message()}]${
                                response.errorBody()?.string()
                            }",
                        )
                    )
                }
            } else {
                uiState = currentState.copy(
                    inProgress = false, isCompleted = null, errorData = ErrorData(
                        titleId = R.string.ResponseErrors_PasswordUpdateError_COPY,
                        messageId = R.string.ResponseErrors_InvalidPasswordHash_COPY,
                    )
                )
            }
        } catch (e: Exception) {
            uiState = currentState.copy(
                inProgress = false, errorData = ErrorData(
                    titleId = R.string.ResponseErrors_PasswordUpdateError_COPY,
                    messageId = R.string.Generic_RequestError_Description_COPY,
                    messageArg = e.message ?: e.javaClass.simpleName,
                )
            )
            Timber.e(e, "Failed to update password")
        }
    }
}