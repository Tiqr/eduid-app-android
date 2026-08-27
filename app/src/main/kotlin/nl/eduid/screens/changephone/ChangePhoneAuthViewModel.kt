package nl.eduid.screens.changephone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.assist.UnauthorizedException
import nl.eduid.di.assist.toErrorData
import org.tiqr.data.model.AuthenticationChallenge
import org.tiqr.data.model.AuthenticationCompleteFailure
import org.tiqr.data.model.AuthenticationCompleteRequest
import org.tiqr.data.model.ChallengeCompleteResult
import org.tiqr.data.model.ChallengeParseResult
import org.tiqr.data.model.SecretCredential
import org.tiqr.data.repository.AuthenticationRepository
import timber.log.Timber
import javax.inject.Inject

enum class VerificationMethod { BIOMETRIC, PIN }

data class ChangePhoneAuthUiState(
    val inProgress: Boolean = false,
    val errorData: ErrorData? = null,
    /** When true, dismissing the current error leaves the flow (e.g. the identity got blocked). */
    val abortOnErrorDismiss: Boolean = false,
    /** One-shot: the challenge is ready and the user must confirm using this method. */
    val verifyWith: VerificationMethod? = null,
    /** One-shot: the user was authenticated successfully. */
    val isVerified: Unit? = null,
    /** Incremented each time a PIN was rejected, so the PIN screen can clear its input. */
    val pinRejections: Int = 0,
)

/**
 * Shared between the explanation and PIN screens of the change phone number flow: the user must complete a
 * tiqr authentication, started by the server, before being allowed to change the SMS recovery number.
 */
@HiltViewModel
class ChangePhoneAuthViewModel @Inject constructor(
    private val assistant: DataAssistant,
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePhoneAuthUiState())
    val uiState: StateFlow<ChangePhoneAuthUiState> = _uiState.asStateFlow()

    private var challenge: AuthenticationChallenge? = null
    private var currentJob: Job? = null

    fun startAuthentication(canUseBiometric: Boolean) {
        if (currentJob?.isActive == true) return
        _uiState.update { it.copy(inProgress = true, errorData = null) }
        currentJob = viewModelScope.launch {
            val startResult = assistant.startAuthentication()
            val url = startResult.getOrElse { error ->
                Timber.e(error, "Failed to start authentication")
                showError(if (error is UnauthorizedException) error.toErrorData() else verificationFailedError())
                return@launch
            }.url
            if (url == null) {
                Timber.e("No authentication URL received")
                showError(verificationFailedError())
                return@launch
            }
            when (val parsed = authenticationRepository.parseChallenge(url)) {
                is ChallengeParseResult.Success -> {
                    challenge = parsed.value
                    val method = if (canUseBiometric && parsed.value.identity?.biometricInUse == true) {
                        VerificationMethod.BIOMETRIC
                    } else {
                        VerificationMethod.PIN
                    }
                    _uiState.update { it.copy(inProgress = false, verifyWith = method) }
                }

                is ChallengeParseResult.Failure -> {
                    Timber.e("Failed to parse authentication challenge: ${parsed.failure.message}")
                    showError(ErrorData(title = parsed.failure.title, message = parsed.failure.message))
                }
            }
        }
    }

    fun completeWithPin(pin: String) = complete(SecretCredential.pin(pin))

    fun completeWithBiometric() = complete(SecretCredential.biometric())

    private fun complete(credential: SecretCredential) {
        val currentChallenge = challenge ?: return
        if (currentJob?.isActive == true) return
        _uiState.update { it.copy(inProgress = true, errorData = null) }
        currentJob = viewModelScope.launch {
            val request = AuthenticationCompleteRequest(currentChallenge, credential.password, credential.type)
            when (val result = authenticationRepository.completeChallenge(request)) {
                ChallengeCompleteResult.Success -> _uiState.update { it.copy(inProgress = false, isVerified = Unit) }
                is ChallengeCompleteResult.Failure -> {
                    val failure = result.failure
                    Timber.e("Failed to complete authentication challenge: ${failure.message}")
                    if (failure is AuthenticationCompleteFailure && failure.reason == AuthenticationCompleteFailure.Reason.INVALID_RESPONSE) {
                        _uiState.update {
                            it.copy(
                                inProgress = false,
                                errorData = ErrorData(title = failure.title, message = failure.message),
                                abortOnErrorDismiss = failure.remainingAttempts == 0,
                                pinRejections = it.pinRejections + 1,
                            )
                        }
                    } else {
                        showError(verificationFailedError())
                    }
                }
            }
        }
    }

    fun dismissError() = _uiState.update { it.copy(errorData = null, abortOnErrorDismiss = false) }

    fun clearVerifyWith() = _uiState.update { it.copy(verifyWith = null) }

    fun clearVerified() = _uiState.update { it.copy(isVerified = null) }

    private fun showError(errorData: ErrorData?) = _uiState.update { it.copy(inProgress = false, errorData = errorData) }

    private fun verificationFailedError() = ErrorData(
        titleId = R.string.Generic_RequestError_Title_COPY,
        messageId = R.string.ChangeSMSRecovery_VerificationFailed_COPY,
    )
}
