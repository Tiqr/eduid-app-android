package nl.eduid.screens.twofactorkeydelete

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import org.tiqr.data.repository.IdentityRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TwoFactorKeyDeleteViewModel @Inject constructor(val identityRepository: IdentityRepository) :
    ViewModel() {

    val uiState = MutableLiveData(UiState())

    fun deleteKey(keyId: String) = viewModelScope.launch {
        uiState.postValue(uiState.value?.copy(inProgress = true))
        val identity = identityRepository.identity(keyId).firstOrNull()
        identity?.let {
            try {
                identityRepository.delete(it.identity)
                uiState.postValue(uiState.value?.copy(inProgress = false, completed = Unit))
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete key")
                val message = e.localizedMessage ?: e.message
                uiState.value?.copy(
                    inProgress = false,
                    errorData = ErrorData("Failed to delete key", "Could not delete key: $message")
                )
            }
        } ?: uiState.postValue(
            uiState.value?.copy(
                inProgress = false, errorData = ErrorData("Cannot find key", "Key not found")
            )
        )
    }

    fun clearErrorMessage() {
        uiState.value = uiState.value?.copy(errorData = null)
    }

    fun clearCompleted() {
        uiState.value = uiState.value?.copy(completed = null)
    }
}