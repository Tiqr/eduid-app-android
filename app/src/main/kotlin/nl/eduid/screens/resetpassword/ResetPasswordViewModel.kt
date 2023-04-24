package nl.eduid.screens.resetpassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repository: PersonalInfoRepository,
) : ViewModel() {

    val uiState = MutableLiveData(UiState())

    init {
        viewModelScope.launch {
            uiState.postValue(uiState.value?.copy(inProgress = true))
            val userDetails = repository.getUserDetails()
            val hasPassword = userDetails?.hasPasswordSet() ?: false
            val password = if (hasPassword) Password.Change else Password.Add
            uiState.postValue(
                uiState.value?.copy(
                    inProgress = false,
                    password = password,
                    emailUsed = userDetails?.email ?: ""
                )
            )
        }
    }

    fun resetPasswordLink() = viewModelScope.launch {
        uiState.postValue(uiState.value?.copy(inProgress = true, errorData = null))
        val userDetails = repository.resetPasswordLink()
        if (userDetails != null) {
            uiState.postValue(
                uiState.value?.copy(
                    inProgress = false,
                    errorData = null,
                    requestCompleted = Unit
                )
            )
        } else {
            uiState.postValue(
                uiState.value?.copy(
                    inProgress = false, errorData = ErrorData(
                        "Failed to request password link",
                        "Could not request reset password link"
                    ), requestCompleted = Unit
                )
            )
        }
    }

    fun clearErrorData() {
        uiState.value = uiState.value?.copy(errorData = null)
    }

    fun clearCompleted() {
        uiState.value = uiState.value?.copy(requestCompleted = null)
    }
}