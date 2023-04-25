package nl.eduid.screens.resetpasswordconfirm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
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
    val uiState = MutableLiveData(UiState())

    init {
        passwordHash = savedStateHandle.get<String>(ConfigurePassword.Form.passwordHashArg) ?: ""
    }

    fun onNewPasswordInput(newValue: String) {
        uiState.value = uiState.value?.copy(newPasswordInput = newValue)
    }

    fun onConfirmPasswordInput(newValue: String) {
        uiState.value = uiState.value?.copy(confirmPasswordInput = newValue)
    }

    fun clearErrorState() {
        uiState.value = uiState.value?.copy(errorData = null, isCompleted = null)
    }


    fun onResetPasswordClicked() {
        val currentState = uiState.value ?: UiState()
        if (currentState.passwordIsValid() && passwordHash.isNotEmpty()) {
            uiState.value = currentState.copy(inProgress = true)
            changePassword(currentState, newPassword = currentState.newPasswordInput)
        } else {
            val detailMessage =
                if (!currentState.passwordIsValid()) "Passwords do not match." else "No valid password has was received."
            uiState.value = currentState.copy(
                errorData = ErrorData(
                    "Cannot update password", detailMessage
                )
            )
        }
    }

    fun onDeletePasswordClicked() {
        val currentState = uiState.value ?: UiState()
        uiState.value = currentState.copy(inProgress = true)
        changePassword(currentState, newPassword = "")
    }

    fun completedShown() {
        uiState.value = uiState.value?.copy(isCompleted = null)
    }

    private fun changePassword(currentState: UiState, newPassword: String) {
        viewModelScope.launch {
            try {
                val hashIsValidResponse = eduIdApi.checkHashIsValid(passwordHash)
                if (hashIsValidResponse.isSuccessful && hashIsValidResponse.body() == true) {
                    val response = eduIdApi.updatePassword(
                        UpdatePasswordRequest(
                            newPassword = newPassword, hash = passwordHash
                        )
                    )
                    if (response.isSuccessful) {
                        uiState.postValue(
                            currentState.copy(
                                inProgress = false, isCompleted = Unit, errorData = null
                            )
                        )
                    } else {
                        uiState.postValue(
                            currentState.copy(
                                inProgress = false, isCompleted = null, errorData = ErrorData(
                                    title = "Failed to update password",
                                    message = "Cannot update password"
                                )
                            )
                        )
                    }
                } else {
                    uiState.postValue(
                        currentState.copy(
                            inProgress = false, isCompleted = null, errorData = ErrorData(
                                title = "Failed to update password",
                                message = "Invalid password hash, resend email link."
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                uiState.postValue(
                    currentState.copy(
                        inProgress = false, errorData = ErrorData("Failed to update password", "")
                    )
                )
                Timber.e(e, "Failed to update password")
            }
        }
    }
}