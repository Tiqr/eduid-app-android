package nl.eduid.screens.removeapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.assist.SaveableResult
import nl.eduid.di.assist.UnauthorizedException
import nl.eduid.di.assist.toErrorData
import timber.log.Timber
import javax.inject.Inject

data class RemoveMobileAppUiState(
    val inProgress: Boolean = false,
    val errorData: ErrorData? = null,
    /** Masked phone number the deactivation code was sent to; set once the code has been requested. */
    val codeSentTo: String? = null,
)

@HiltViewModel
class RemoveMobileAppViewModel @Inject constructor(
    private val assistant: DataAssistant,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemoveMobileAppUiState())
    val uiState: StateFlow<RemoveMobileAppUiState> = _uiState.asStateFlow()

    fun requestDeactivationCode() {
        if (_uiState.value.inProgress) return
        _uiState.update { it.copy(inProgress = true) }
        viewModelScope.launch {
            val details = (assistant.observableDetails.firstOrNull() as? SaveableResult.Success)?.data
            val knownPhoneNumber = "*${details?.registration?.phoneNumber.orEmpty()}"
            assistant.requestDeactivationCode().fold(
                onSuccess = { _uiState.update { it.copy(inProgress = false, codeSentTo = knownPhoneNumber) } },
                onFailure = { error ->
                    Timber.e(error, "Failed to request deactivation code")
                    val errorData = if (error is UnauthorizedException) {
                        error.toErrorData()
                    } else {
                        ErrorData(
                            titleId = R.string.ResponseErrors_DeactivationError_Title_COPY,
                            messageId = R.string.ResponseErrors_DeactivationError_Description_COPY,
                            messageArg = knownPhoneNumber,
                        )
                    }
                    _uiState.update { it.copy(inProgress = false, errorData = errorData) }
                },
            )
        }
    }

    fun dismissError() = _uiState.update { it.copy(errorData = null) }

    fun clearCodeSent() = _uiState.update { it.copy(codeSentTo = null) }
}
