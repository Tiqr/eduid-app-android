package nl.eduid.screens.twofactorkeydelete

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import org.tiqr.data.repository.IdentityRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TwoFactorKeyDeleteViewModel @Inject constructor(val identityRepository: IdentityRepository) :
    ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    fun deleteKey(keyId: String) = viewModelScope.launch {
        uiState = uiState.copy(inProgress = true)
        val identity = identityRepository.identity(keyId).firstOrNull()
        uiState = identity?.let { it ->
            try {
                identityRepository.delete(it.identity)
                uiState.copy(inProgress = false, isCompleted = Unit)
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete key")
                val message = "${e.javaClass.simpleName}: ${e.localizedMessage ?: e.message}"
                uiState.copy(
                    inProgress = false, errorData = ErrorData(
                        titleId = R.string.Generic_RequestError_Title_COPY,
                        messageId = R.string.Generic_RequestError_Description_COPY,
                        messageArg = message
                    )
                )
            }
        } ?: uiState.copy(
            inProgress = false, errorData = ErrorData(
                titleId = R.string.Generic_RequestError_Title_COPY,
                messageId = R.string.ResponseErrors_DeleteKeyLostError_COPY,
            )
        )
    }

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }

    fun clearCompleted() {
        uiState = uiState.copy(isCompleted = null)
    }
}