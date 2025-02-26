package nl.eduid.screens.verifywithid.input

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.model.ControlCode
import nl.eduid.di.model.ControlCodeRequest
import nl.eduid.graphs.VerifyIdentityWithIdInput
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import javax.inject.Inject

@HiltViewModel
class VerifyWithIdInputViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: PersonalInfoRepository
): ViewModel() {

    data class UiState(
        val editCode: ControlCode? = null,
        val isLoading: Boolean = false,
        val createdControlCode: ControlCode? = null,
        val errorData: ErrorData? = null,
    )

    var uiState by mutableStateOf(UiState(editCode = VerifyIdentityWithIdInput.deserializeCode(savedStateHandle[VerifyIdentityWithIdInput.codeArg])))
        private set

    fun generateCode(request: ControlCodeRequest) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.createControlCode(request)
                uiState = uiState.copy(createdControlCode = response)
            } catch (ex: Exception) {
                uiState = uiState.copy(isLoading = false, errorData = ErrorData(
                    titleId = R.string.ResponseErrors_GenerateControlCodeError_COPY,
                    message = ex.message ?: "Unknown error"
                ))
            }
        }
    }

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }

}