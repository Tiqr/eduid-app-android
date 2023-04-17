package nl.eduid.screens.resetpassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.di.api.EduIdApi
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(private val eduid: EduIdApi) : ViewModel() {

    val uiState = MutableLiveData(UiState())

    fun resetPasswordLink() = viewModelScope.launch {
        uiState.postValue(uiState.value?.copy(inProgress = true, errorData = null))
        try {
            val response = eduid.resetPasswordLink()
            if (response.isSuccessful) {
                uiState.postValue(
                    uiState.value?.copy(
                        inProgress = false,
                        errorData = null,
                        requestCompleted = Unit
                    )
                )
            } else {
                uiState.postValue(
                    uiState.value?.copy(
                        inProgress = false, errorData = ErrorData(
                            "Failed to request password link",
                            "Could not request reset password link"
                        ), requestCompleted = Unit
                    )
                )
            }

        } catch (e: Exception) {
            Timber.e(e, "Failed to reset password link")
            uiState.postValue(
                uiState.value?.copy(
                    inProgress = false,
                    errorData = ErrorData(
                        "Failed to request password link",
                        "Could not request reset password link"
                    )
                )
            )
        }
    }

    fun clearErrorData() {
        uiState.value = uiState.value?.copy(errorData = null)
    }
}