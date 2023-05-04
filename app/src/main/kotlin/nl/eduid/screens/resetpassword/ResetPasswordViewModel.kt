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
        val userDetails = repository.resetPasswordLink()
        if (userDetails != null) {
            uiState =
                uiState.copy(
                    inProgress = false,
                    errorData = null,
                    isCompleted = Unit
                )

        } else {
            uiState =
                uiState.copy(
                    inProgress = false,
                    errorData = ErrorData(
                        titleId = R.string.err_title_request_fail,
                        messageId = R.string.err_msg_cannot_request_reset_pass,
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