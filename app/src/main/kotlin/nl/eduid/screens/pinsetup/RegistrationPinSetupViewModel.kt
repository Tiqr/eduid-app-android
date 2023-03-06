package nl.eduid.screens.pinsetup

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.BaseViewModel
import nl.eduid.graphs.ExistingAccount
import nl.eduid.screens.scan.ErrorData
import nl.eduid.ui.PIN_MAX_LENGTH
import org.tiqr.core.util.extensions.biometricUsable
import org.tiqr.data.model.ChallengeCompleteResult
import org.tiqr.data.model.EnrollmentChallenge
import org.tiqr.data.model.EnrollmentCompleteRequest
import org.tiqr.data.repository.EnrollmentRepository
import timber.log.Timber
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class RegistrationPinSetupViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, moshi: Moshi, private val repository: EnrollmentRepository
) : BaseViewModel(moshi) {
    val uiState: MutableLiveData<RegistrationPinUiState> = MutableLiveData(
        RegistrationPinUiState()
    )

    val challenge: EnrollmentChallenge?

    init {
        val enrolChallenge =
            savedStateHandle.get<String>(ExistingAccount.RegistrationPinSetup.registrationChallengeArg)
                ?: ""
        val decoded = URLDecoder.decode(enrolChallenge, Charsets.UTF_8.name())
        val adapter = moshi.adapter(EnrollmentChallenge::class.java)
        challenge = try {
            adapter.fromJson(decoded)
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse enrollment challenge")
            uiState.value = uiState.value?.copy(
                errorData = ErrorData(
                    title = "Failed to parse challenge",
                    message = "Could not parse enrollment challenge"
                )
            )
            null
        }

    }

    fun onPinChange(inputCode: String, pinStep: PinStep) {
        if (pinStep is PinStep.PinCreate) {
            uiState.value = uiState.value?.copy(pinValue = inputCode, isPinInvalid = false)
        } else {
            uiState.value = uiState.value?.copy(pinConfirmValue = inputCode, isPinInvalid = false)
        }
    }

    fun submitPin(context: Context, currentStep: PinStep) {
        if (currentStep is PinStep.PinCreate) {
            val createdPin = uiState.value?.pinValue ?: ""
            val isInvalid = createdPin.length != PIN_MAX_LENGTH
            uiState.value = uiState.value?.copy(isPinInvalid = isInvalid)
            if (createdPin.length == PIN_MAX_LENGTH) {
                uiState.value =
                    uiState.value?.copy(pinStep = PinStep.PinConfirm, isPinInvalid = false)
            }
        } else {
            val confirmPin = uiState.value?.pinConfirmValue ?: ""
            val createdPin = uiState.value?.pinValue ?: ""
            val pinConfirmed = confirmPin == createdPin
            uiState.value = uiState.value?.copy(isPinInvalid = !pinConfirmed)
            if (pinConfirmed) {
                enroll(context, createdPin)
            }
        }
    }

    private fun enroll(context: Context, password: String) = viewModelScope.launch {
        val currentChallenge = challenge ?: return@launch
        val result =
            repository.completeChallenge(EnrollmentCompleteRequest(currentChallenge, password))
        when (result) {
            is ChallengeCompleteResult.Failure -> {
                uiState.postValue(
                    uiState.value?.copy(
                        errorData = ErrorData(
                            result.failure.title, result.failure.message
                        )
                    )
                )
            }
            ChallengeCompleteResult.Success -> {
                uiState.value =
                    uiState.value?.copy(promptBiometric = context.biometricUsable() && currentChallenge.identity.biometricOfferUpgrade)
            }
        }
    }

    fun handleBackNavigation(closePinSetupFlow: () -> Unit) {
        val currentStep = uiState.value?.pinStep ?: PinStep.PinCreate
        if (currentStep is PinStep.PinCreate) {
            closePinSetupFlow()
        } else {
            uiState.value = RegistrationPinUiState()
        }
    }

    fun dismissError() {
        uiState.value = uiState.value?.copy(errorData = null)
    }
}