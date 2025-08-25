package nl.eduid.screens.resetpassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repository: PersonalInfoRepository,
) : ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    init {
        viewModelScope.launch {
            uiState = uiState.copy(inProgress = true)
            val userDetails = repository.getUserDetails()
            val hasPassword = userDetails?.hasPasswordSet() ?: false
            val password = if (hasPassword) Password.Change else Password.Add
            uiState =
                uiState.copy(
                    inProgress = false,
                    password = password,
                    emailUsed = userDetails?.email ?: ""
                )
        }
    }

    fun resetPasswordLink() = viewModelScope.launch {
        uiState = uiState.copy(inProgress = true, errorData = null)
        try {
            repository.generatePasswordCode()
            uiState =
                uiState.copy(
                    errorData = null,
                    isCompleted = Unit
                )
        } catch (ex: Exception) {
            Timber.w(ex, "Failed to send code via email to change the password")
            uiState =
                uiState.copy(
                    errorData = ErrorData(
                        titleId = R.string.Generic_RequestError_Title_COPY,
                        messageId = R.string.ResponseErrors_RequestResetLinkError_COPY,
                    ),
                    isCompleted = null
                )
        }
    }

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }

    fun clearCompleted() {
        uiState = uiState.copy(isCompleted = null)
    }
}