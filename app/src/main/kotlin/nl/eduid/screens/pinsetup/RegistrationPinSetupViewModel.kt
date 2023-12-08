package nl.eduid.screens.pinsetup

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.BaseViewModel
import nl.eduid.CheckRecovery
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.graphs.Account
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import nl.eduid.ui.PIN_MAX_LENGTH
import org.tiqr.core.util.extensions.biometricUsable
import org.tiqr.data.model.ChallengeCompleteResult
import org.tiqr.data.model.EnrollmentChallenge
import org.tiqr.data.model.EnrollmentCompleteRequest
import org.tiqr.data.repository.EnrollmentRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegistrationPinSetupViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    moshi: Moshi,
    personal: PersonalInfoRepository,
    private val enrollRepository: EnrollmentRepository,
) : BaseViewModel(moshi) {
    var uiState by mutableStateOf(UiState())
        private set

    private val challenge: EnrollmentChallenge?
    private val checkRecovery =
        CheckRecovery(personal = personal)

    init {
        val enrolChallenge =
            savedStateHandle.get<String>(Account.EnrollPinSetup.enrollChallenge)
                ?: ""
        val challengeUrl = Uri.decode(enrolChallenge)
        val adapter = moshi.adapter(EnrollmentChallenge::class.java)
        challenge = try {
            adapter.fromJson(challengeUrl)
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse enrollment challenge")
            uiState = uiState.copy(
                errorData = ErrorData(
                    titleId = R.string.ResponseErrors_InvalidChallenge_Title_COPY,
                    messageId = R.string.ResponseErrors_InvalidChallenge_Description_COPY,
                )
            )
            null
        }

    }

    fun onPinChange(inputCode: String, pinStep: PinStep) {
        uiState = if (pinStep is PinStep.PinCreate) {
            uiState.copy(pinValue = inputCode, isPinInvalid = false)
        } else {
            uiState.copy(pinConfirmValue = inputCode, isPinInvalid = false)
        }
    }

    fun submitPin(context: Context, currentStep: PinStep) {
        uiState = uiState.copy(isProcessing = true)
        if (currentStep is PinStep.PinCreate) {
            val createdPin = uiState.pinValue
            uiState = if (createdPin.length == PIN_MAX_LENGTH) {
                uiState.copy(
                    pinStep = PinStep.PinConfirm,
                    isPinInvalid = false,
                    isProcessing = false
                )
            } else {
                uiState.copy(isPinInvalid = true, isProcessing = false)
            }
        } else {
            val confirmPin = uiState.pinConfirmValue
            val createdPin = uiState.pinValue
            val pinConfirmed = confirmPin == createdPin
            if (pinConfirmed) {
                enroll(context, createdPin)
            } else {
                uiState = uiState.copy(isPinInvalid = true, isProcessing = false)
            }
        }
    }

    private fun enroll(context: Context, password: String) = viewModelScope.launch {
        val currentChallenge = challenge ?: return@launch
        val result =
            enrollRepository.completeChallenge(
                EnrollmentCompleteRequest(
                    currentChallenge,
                    password
                )
            )
        when (result) {
            is ChallengeCompleteResult.Failure -> {
                uiState = uiState.copy(
                    errorData = ErrorData(
                        result.failure.title, result.failure.message
                    ),
                    isProcessing = false
                )
            }

            ChallengeCompleteResult.Success -> {
                val nextStep = calculateNextStep(context, currentChallenge)
                uiState =
                    uiState.copy(
                        nextStep = nextStep,
                        isProcessing = false
                    )
            }
        }
    }

    private suspend fun calculateNextStep(
        context: Context,
        currentChallenge: EnrollmentChallenge,
    ): NextStep {
        return if (context.biometricUsable() && currentChallenge.identity.biometricOfferUpgrade) {
            NextStep.PromptBiometric(currentChallenge, uiState.pinConfirmValue)
        } else {
            val shouldAppDoRecovery =
                checkRecovery.shouldAppDoRecoveryForIdentity(currentChallenge.identity.identifier)
            if (shouldAppDoRecovery) {
                NextStep.Recovery
            } else {
                NextStep.RecoveryInBrowser
            }
        }
    }

    fun handleBackNavigation(closePinSetupFlow: () -> Unit) {
        val currentStep = uiState.pinStep
        if (currentStep is PinStep.PinCreate) {
            closePinSetupFlow()
        } else {
            uiState = UiState()
        }
    }

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }
}