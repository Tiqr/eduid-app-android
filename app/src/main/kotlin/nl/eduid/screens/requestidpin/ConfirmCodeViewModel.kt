package nl.eduid.screens.requestidpin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.screens.requestidrecovery.RecoveryRepository
import nl.eduid.screens.requestidrecovery.UiState
import nl.eduid.screens.scan.ErrorData
import javax.inject.Inject

@HiltViewModel
class ConfirmCodeViewModel @Inject constructor(private val repository: RecoveryRepository) :
    ViewModel() {

    val uiState = MutableLiveData(UiState())

    fun onCodeChange(newValue: String) {
        uiState.value = uiState.value?.copy(input = newValue)
    }

    fun confirmPhoneCode() = viewModelScope.launch {
        val currentState = uiState.value ?: return@launch
        uiState.postValue(currentState.copy(inProgress = true))
        val success = repository.confirmPhoneCode(currentState.input)
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