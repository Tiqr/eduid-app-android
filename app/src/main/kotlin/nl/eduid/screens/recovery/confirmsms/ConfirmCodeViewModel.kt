package nl.eduid.screens.recovery.confirmsms

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
import nl.eduid.di.repository.StorageRepository
import nl.eduid.graphs.PhoneNumberRecovery
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import nl.eduid.screens.recovery.UiState
import javax.inject.Inject

@HiltViewModel
class ConfirmCodeViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val repository: PersonalInfoRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set
    private val isDeactivation: Boolean

    init {
        isDeactivation =
            savedStateHandle.get<Boolean>(PhoneNumberRecovery.ConfirmCode.isDeactivationArg)
                ?: false
    }

    fun onCodeChange(newValue: String) {
        uiState = uiState.copy(input = newValue)
    }

    fun confirmPhoneCode() = viewModelScope.launch {
        uiState = uiState.copy(inProgress = true)
        val success = if (isDeactivation) {
            repository.deactivateApp(uiState.input)
        } else {
            repository.confirmPhoneCode(uiState.input)
        }
        if (success && isDeactivation) {
            storageRepository.setDidDeactivateLinkedDevice(true)
        }
        val newState = if (success) {
            uiState.copy(inProgress = false, errorData = null, isCompleted = Unit)
        } else {
            uiState.copy(
                inProgress = false, errorData =
                ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = R.string.ResponseErrors_ConfirmSMSError_COPY,
                )
            )
        }
        uiState = newState
    }

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }

    fun clearCompleted() {
        uiState = uiState.copy(isCompleted = null)
    }
}