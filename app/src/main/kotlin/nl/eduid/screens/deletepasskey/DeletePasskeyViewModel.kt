package nl.eduid.screens.deletepasskey

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.assist.toErrorData
import nl.eduid.graphs.DeletePasskeyRoute
import nl.eduid.screens.recovery.UiState
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DeletePasskeyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val assistant: DataAssistant,
) : ViewModel() {

    private val passkey = DeletePasskeyRoute.deserializePasskey(savedStateHandle.get<String>(DeletePasskeyRoute.passkeyArg))
    val passkeyName: String = passkey?.name.orEmpty()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun deletePasskey() {
        if (_uiState.value.inProgress) return
        val credential = passkey ?: run {
            _uiState.update { it.copy(errorData = IllegalStateException("Missing passkey").toErrorData()) }
            return
        }
        _uiState.update { it.copy(inProgress = true) }
        viewModelScope.launch {
            assistant.removeCredential(credential).fold(
                onSuccess = { _uiState.update { it.copy(inProgress = false, isCompleted = Unit) } },
                onFailure = { error ->
                    Timber.e(error, "Failed to delete passkey")
                    _uiState.update { it.copy(inProgress = false, errorData = error.toErrorData()) }
                },
            )
        }
    }

    fun dismissError() = _uiState.update { it.copy(errorData = null) }

    fun clearCompleted() = _uiState.update { it.copy(isCompleted = null) }
}
