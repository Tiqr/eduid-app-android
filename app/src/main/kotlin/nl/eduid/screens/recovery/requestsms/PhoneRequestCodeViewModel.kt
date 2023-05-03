package nl.eduid.screens.recovery.requestsms

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
import nl.eduid.screens.recovery.UiState
import javax.inject.Inject

@HiltViewModel
class PhoneRequestCodeViewModel @Inject constructor(private val repository: PersonalInfoRepository) :
    ViewModel() {
    var uiState: UiState by mutableStateOf(UiState())
        private set

    fun onPhoneNumberChange(newValue: String) {
        uiState = uiState.copy(input = newValue)
    }

    fun requestPhoneCode() = viewModelScope.launch {
        uiState = uiState.copy(inProgress = true)
        val success = repository.requestPhoneCode(uiState.input)
        uiState = if (success) {
            uiState.copy(
                inProgress = false, errorData = null, isCompleted = Unit
            )
        } else {
            uiState.copy(
                inProgress = false,
                errorData = ErrorData(
                    titleId = R.string.err_title_request_fail,
                    messageId = R.string.err_msg_request_sms_validation_fail,
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