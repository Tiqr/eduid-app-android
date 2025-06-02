package nl.eduid.screens.emailcodeentry

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
import nl.eduid.di.repository.EduIdRepository
import nl.eduid.graphs.EmailCodeEntry
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class EmailCodeEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val eduIdRepository: EduIdRepository,
) : ViewModel() {

    val userEmail: String = savedStateHandle.get<String>(EmailCodeEntry.emailArg) ?: ""
    val codeHash: String = savedStateHandle.get<String>(EmailCodeEntry.codeHashArg) ?: ""

    var uiState by mutableStateOf(UiState())
        private set

    fun checkEmailCode(code: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = eduIdRepository.verifyOneTimeCode(codeHash, code)
            val handledCodes = setOf(201, 400, 401, 403)
            uiState = uiState.copy(
                isLoading = false,
                isCodeCorrect = result == 201,
                isCodeExpired = result == 400,
                isCodeIncorrect = result == 401,
                isRateLimited = result == 403,
                errorData = if (result !in handledCodes) {
                    ErrorData(
                        titleId = R.string.ResponseErrors_GeneralRequestError_COPY,
                        messageId = R.string.ResponseErrors_VerifyOneTimeCodeError_COPY
                    )
                } else null
            )
        }
    }

    fun resendEmailCode() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            try {
                eduIdRepository.resendOneTimeCode(codeHash)
                uiState = uiState.copy(isLoading = false, didResendEmail = true)
            } catch (ex: Exception) {
                Timber.w(ex, "Failed to resend one-time code email")
                uiState = uiState.copy(
                    isLoading = false,
                    errorData = ErrorData(
                        titleId = R.string.ResponseErrors_GeneralRequestError_COPY,
                        messageId = R.string.ResponseErrors_SendOneTimeCodeError_COPY
                    )
                )
            }
            uiState = uiState.copy(isLoading = false)
        }
    }

    fun dismissError() {
        uiState = uiState.copy(
            isCodeIncorrect = false,
            isCodeExpired = false,
            isRateLimited = false,
            errorData = null
        )
    }
}

data class UiState(
    val isLoading: Boolean = false,
    val errorData: ErrorData? = null,
    val isCodeIncorrect: Boolean = false,
    val isCodeCorrect: Boolean = false,
    val isRateLimited: Boolean = false,
    val isCodeExpired: Boolean = false,
    val didResendEmail: Boolean = false
)