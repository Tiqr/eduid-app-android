package nl.eduid.screens.editemail

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.di.api.EduIdApi
import nl.eduid.di.model.EmailChangeRequest
import javax.inject.Inject

@HiltViewModel
class EditEmailViewModel @Inject constructor(private val eduIdApi: EduIdApi) : ViewModel() {
    val emailInput = MutableLiveData("")
    val emailValid = MutableLiveData(false)
    val uiState = MutableLiveData<UiState>(UiState.Idle)
    val uiError = MutableLiveData("")

    fun onEmailChange(newValue: String) {
        emailInput.value = newValue
        emailValid.value = Patterns.EMAIL_ADDRESS.matcher(emailInput.value?.trim() ?: "").matches()
    }

    fun requestEmailChangeClicked(newEmail: String, onSuccess: (email: String) -> Unit) {
        viewModelScope.launch {
            try {
                uiState.value = UiState.Loading
                val enrollResponse = eduIdApi.requestEmailChange(EmailChangeRequest(newEmail))
                val response = enrollResponse.body()
                if (enrollResponse.isSuccessful && response != null) {
                    uiState.value = UiState.Idle
                    onSuccess.invoke(emailInput.value?.trim() ?: "")
                } else {
                    uiState.value = UiState.Idle
                    uiError.value = "Failed to request email change"
                }
            } catch (e: Exception) {
                uiState.value = UiState.Idle
                uiError.value = "Failed to request email change"
            }
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
    }
}