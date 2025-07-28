package nl.eduid.screens.emailcodeentry

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.repository.EduIdRepository
import nl.eduid.graphs.EmailCodeEntry
import timber.log.Timber
import javax.inject.Inject
import androidx.core.net.toUri


@HiltViewModel
class EmailCodeEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val eduIdRepository: EduIdRepository,
) : ViewModel() {

    companion object {
        const val KEY_CODE_RESULT_HASH = "code_result_hash"
    }

    enum class CodeContext {
        Registration, ChangeEmail, ChangePassword
    }

    val userEmail: String = savedStateHandle.get<String>(EmailCodeEntry.emailArg) ?: ""
    val codeHash: String = savedStateHandle.get<String>(EmailCodeEntry.codeHashArg) ?: ""
    val codeContext: CodeContext =
        CodeContext.valueOf(savedStateHandle.get<String>(EmailCodeEntry.codeContextArg) ?: CodeContext.Registration.name)

    var uiState by mutableStateOf(UiState())
        private set

    fun checkEmailCode(code: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val codeAndResponse = when (codeContext) {
                CodeContext.Registration -> {
                    eduIdRepository.verifyOneTimeCode(codeHash, code)
                }
                CodeContext.ChangeEmail -> {
                    eduIdRepository.verifyEmailCode(code)
                }
                else -> {
                    eduIdRepository.verifyPasswordCode(code)
                }
            }
            val code = codeAndResponse.first
            val response = codeAndResponse.second
            val handledCodes = setOf(200, 201, 400, 401, 403)
            val goBackWithHash = response?.hash
            uiState = uiState.copy(
                isLoading = false,
                isCodeExpired = code == 400,
                isCodeIncorrect = code == 401,
                isRateLimited = code == 403,
                correctCodeLaunchIntent = if (codeContext == CodeContext.Registration) response?.url?.let { createLaunchIntent(it) } else null,
                codeIsCorrectContinueWithHash = goBackWithHash,
                errorData = if (code !in handledCodes) {
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

    fun didShowResentEmailToast() {
        uiState = uiState.copy(didResendEmail = false)
    }
}

data class UiState(
    val isLoading: Boolean = false,
    val errorData: ErrorData? = null,
    val isCodeIncorrect: Boolean = false,
    val isRateLimited: Boolean = false,
    val isCodeExpired: Boolean = false,
    val didResendEmail: Boolean = false,
    val correctCodeLaunchIntent: Intent? = null,
    val codeIsCorrectContinueWithHash: String? = null
)

private fun createLaunchIntent(url: String): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = url.toUri()
    return intent
}