package nl.eduid.screens.recovery.requestsms

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
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.assist.UnauthorizedException
import nl.eduid.di.assist.toErrorData
import nl.eduid.graphs.PhoneNumberRecovery
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import nl.eduid.screens.recovery.UiState
import javax.inject.Inject

@HiltViewModel
class PhoneRequestCodeViewModel @Inject constructor(
    private val repository: PersonalInfoRepository,
    private val assistant: DataAssistant,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var uiState: UiState by mutableStateOf(UiState())
        private set

    /** True when an already registered user changes their SMS recovery number, after re-authenticating. */
    val isReVerification: Boolean = savedStateHandle.get<Boolean>(PhoneNumberRecovery.isReVerificationArg) ?: false

    fun onPhoneNumberChange(newValue: String) {
        uiState = uiState.copy(input = newValue)
    }

    fun requestPhoneCode() = viewModelScope.launch {
        if (uiState.inProgress) return@launch
        uiState = uiState.copy(inProgress = true)
        if (isReVerification) {
            val result = assistant.resendPhoneCode(uiState.input)
            uiState = result.fold(
                onSuccess = { uiState.copy(inProgress = false, errorData = null, isCompleted = Unit) },
                onFailure = { error ->
                    uiState.copy(
                        inProgress = false,
                        errorData = if (error is UnauthorizedException) error.toErrorData() else requestSmsError(),
                    )
                },
            )
            return@launch
        }
        val success = repository.requestPhoneCode(uiState.input)
        uiState = if (success) {
            uiState.copy(
                inProgress = false, errorData = null, isCompleted = Unit
            )
        } else {
            uiState.copy(
                inProgress = false,
                errorData = requestSmsError(),
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

    private fun requestSmsError() = ErrorData(
        titleId = R.string.Generic_RequestError_Title_COPY,
        messageId = R.string.ResponseErrors_RequestSMSError_COPY,
    )
}
