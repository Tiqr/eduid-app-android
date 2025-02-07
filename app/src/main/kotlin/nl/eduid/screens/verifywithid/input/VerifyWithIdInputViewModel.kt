package nl.eduid.screens.verifywithid.input

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.toErrorData
import nl.eduid.di.model.ControlCode
import nl.eduid.di.model.ControlCodeRequest
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import nl.eduid.screens.pinsetup.UiState
import javax.inject.Inject

@HiltViewModel
class VerifyWithIdInputViewModel @Inject constructor(
    val repository: PersonalInfoRepository
): ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val createdControlCode: ControlCode? = null,
        val errorData: ErrorData? = null,
    )

    var uiState by mutableStateOf(UiState())
        private set

    fun generateCode(request: ControlCodeRequest) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.createControlCode(request)
                uiState = uiState.copy(isLoading = false, createdControlCode = response)
            } catch (ex: Exception) {
                uiState = uiState.copy(isLoading = false, errorData = ErrorData(
                    titleId = R.string.ConfirmIdentityWithIdInput_GenerateError_COPY,
                    message = ex.message ?: "Unknown error"
                ))
            }
        }
    }

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }

}