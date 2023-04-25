package nl.eduid.screens.requestidpin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.graphs.PhoneNumberRecovery
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import nl.eduid.screens.requestidrecovery.UiState
import javax.inject.Inject

@HiltViewModel
class ConfirmCodeViewModel @Inject constructor(
    private val repository: PersonalInfoRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val uiState = MutableLiveData(UiState())
    private val isDeactivation: Boolean

    init {
        isDeactivation =
            savedStateHandle.get<Boolean>(PhoneNumberRecovery.ConfirmCode.isDeactivationArg)
                ?: false

    }

    fun onCodeChange(newValue: String) {
        uiState.value = uiState.value?.copy(input = newValue)
    }

    fun confirmPhoneCode() = viewModelScope.launch {
        val currentState = uiState.value ?: return@launch
        uiState.postValue(currentState.copy(inProgress = true))
        val success = if (isDeactivation) {
            repository.deactivateApp(currentState.input)
        } else {
            repository.confirmPhoneCode(currentState.input)
        }

        val errorData = if (success) {
            null
        } else {
            ErrorData("Failed", "Could not request phone code, please retry")
        }
        uiState.postValue(currentState.copy(inProgress = false, errorData = errorData))
    }

    fun dismissError() {
        uiState.value = uiState.value?.copy(errorData = null)
    }
}