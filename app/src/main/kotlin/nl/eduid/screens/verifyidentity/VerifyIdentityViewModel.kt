package nl.eduid.screens.verifyidentity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerifyIdentityViewModel @Inject constructor(): ViewModel() {
    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }

    var uiState by mutableStateOf(VerifyIdentityData(isLoading = true))
        private set

}