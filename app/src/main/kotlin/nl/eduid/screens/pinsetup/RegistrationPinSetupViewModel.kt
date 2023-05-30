package nl.eduid.screens.pinsetup

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import nl.eduid.BaseViewModel
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.repository.StorageRepository
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
    private val enrollRepository: EnrollmentRepository,
    private val personal: PersonalInfoRepository,
    private val storage: StorageRepository,
) : BaseViewModel(moshi) {
    var uiState by mutableStateOf(UiState())
        private set

    val isAuthorized = storage.isAuthorized.asLiveData()
    private val challenge: EnrollmentChallenge?

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
                    titleId = R.string.err_title_invalid_challenge,
                    messageId = R.string.err_msg_invalid_challenge,
                )
            )
            null
        }

    }

    fun onPinChange(inputCode: String, pinStep: PinStep) {
        if (pinStep is PinStep.PinCreate) {
            uiState = uiState.copy(pinValue = inputCode, isPinInvalid = false)
        } else {
            uiState = uiState.copy(pinConfirmValue = inputCode, isPinInvalid = false)
        }
    }

    fun submitPin(context: Context, currentStep: PinStep) {
        uiState = uiState.copy(isProcessing = true)
        if (currentStep is PinStep.PinCreate) {
            val createdPin = uiState.pinValue
            if (createdPin.length == PIN_MAX_LENGTH) {
                uiState = uiState.copy(
                    pinStep = PinStep.PinConfirm,
                    isPinInvalid = false,
                    isProcessing = false
                )
            } else {
                uiState = uiState.copy(isPinInvalid = true, isProcessing = false)
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
                        promptAuth = storage.isAuthorized.firstOrNull(),
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
        val userDetails = personal.getUserDetails()
        return if (context.biometricUsable() && currentChallenge.identity.biometricOfferUpgrade) {
            NextStep.PromptBiometric(currentChallenge, uiState.pinConfirmValue)
        } else {
            if (userDetails != null) {
                NextStep.Recovery
            } else {
                NextStep.Home
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