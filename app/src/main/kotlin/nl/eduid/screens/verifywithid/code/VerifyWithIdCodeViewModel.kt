package nl.eduid.screens.verifywithid.code

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.assist.toErrorData
import nl.eduid.di.model.ControlCode
import nl.eduid.di.model.ControlCodeRequest
import nl.eduid.graphs.Security
import nl.eduid.graphs.VerifyIdentityWithIdCode
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import nl.eduid.screens.pinsetup.UiState
import javax.inject.Inject

@HiltViewModel
class VerifyWithIdCodeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: PersonalInfoRepository,
    private val assistant: DataAssistant
) : ViewModel() {

    val controlCode = VerifyIdentityWithIdCode.deserializeCode(savedStateHandle.get<String>(VerifyIdentityWithIdCode.codeArg))

    data class UiState(
        val isLoading: Boolean = false,
        val codeDeleted: Boolean = false,
        val errorData: ErrorData? = null
    )

    var uiState by mutableStateOf(UiState())
        private set

    init {
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch (Dispatchers.IO) {
            while (isActive) {
                delay(10_000) // Poll every 10s
                val userDetails = assistant.refreshDetails()
                if (userDetails != null && userDetails.controlCode == null) {
                    // This will navigate to the overview screen, where it will update to the latest state
                    uiState = uiState.copy(codeDeleted = true)
                }
            }
        }
    }

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }

    fun deleteCode() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            val newUserDetails = repository.deleteControlCode()
            if (newUserDetails.isSuccess) {
                assistant.refreshDetails()
                uiState = uiState.copy(codeDeleted = true)
                return@launch
            }
            newUserDetails.exceptionOrNull()?.let { ex ->
                uiState = uiState.copy(
                    isLoading = false,
                    errorData = ex.toErrorData(),
                )
                return@launch
            }
            // No response, no exception? Should not happen, but let's display a generic error to the user
            uiState = uiState.copy(
                isLoading = false,
                errorData = ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = R.string.ResponseErrors_RemoveControlCodeError_COPY,
                ),
            )
        }

    }

}