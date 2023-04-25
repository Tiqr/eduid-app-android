package nl.eduid.screens.requestidrecovery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import javax.inject.Inject

@HiltViewModel
class PhoneRequestCodeViewModel @Inject constructor(private val repository: PersonalInfoRepository) :
    ViewModel() {
    val uiState = MutableLiveData(UiState())

    fun onPhoneNumberChange(newValue: String) {
        uiState.value = uiState.value?.copy(input = newValue)
    }

    fun requestPhoneCode() = viewModelScope.launch {
        val currentState = uiState.value ?: return@launch
        uiState.postValue(currentState.copy(inProgress = true))
        val success = repository.requestPhoneCode(currentState.input)
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