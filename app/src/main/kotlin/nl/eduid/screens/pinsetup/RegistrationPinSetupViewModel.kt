package nl.eduid.screens.pinsetup

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.BaseViewModel
import nl.eduid.RegistrationPinSetup
import nl.eduid.screens.scan.ErrorData
import nl.eduid.ui.PIN_MAX_LENGTH
import org.tiqr.core.util.extensions.biometricUsable
import org.tiqr.data.model.ChallengeCompleteResult
import org.tiqr.data.model.EnrollmentChallenge
import org.tiqr.data.model.EnrollmentCompleteRequest
import org.tiqr.data.repository.EnrollmentRepository
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class RegistrationPinSetupViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    moshi: Moshi,
    private val repository: EnrollmentRepository
) : BaseViewModel(moshi) {
    val pinStep: MutableLiveData<PinStep> = MutableLiveData(PinStep.PinCreate)
    val isPinInvalid = MutableLiveData(false)
    val pinCreate = MutableLiveData("")
    val pinConfirm = MutableLiveData("")
    val promptBiometric: MutableLiveData<Boolean?> = MutableLiveData(null)
    val challenge: MutableLiveData<EnrollmentChallenge?> = MutableLiveData(null)
    val errorData = MutableLiveData<ErrorData?>(null)

    init {
        val enrolChallenge =
            savedStateHandle.get<String>(RegistrationPinSetup.registrationChallengeArg) ?: ""
        val decoded = URLDecoder.decode(enrolChallenge, Charsets.UTF_8.name())
        val adapter = moshi.adapter(EnrollmentChallenge::class.java)
        challenge.value = adapter.fromJson(decoded)
    }

    fun onPinChange(inputCode: String, pinStep: PinStep) {
        isPinInvalid.value = false
        if (pinStep is PinStep.PinCreate) {
            pinCreate.value = inputCode
        } else {
            isPinInvalid.value = false
            pinConfirm.value = inputCode
        }
    }

    fun submitPin(context: Context, currentStep: PinStep) {
        if (currentStep is PinStep.PinCreate) {
            val createdPin = pinCreate.value ?: ""
            val isInvalid = createdPin.length != PIN_MAX_LENGTH
            isPinInvalid.value = isInvalid
            if (createdPin.length == PIN_MAX_LENGTH) {
                pinStep.value = PinStep.PinConfirm
            }
        } else {
            val confirmPin = pinConfirm.value ?: ""
            val createdPin = pinCreate.value ?: ""
            val pinConfirmed = confirmPin == createdPin
            isPinInvalid.value = !pinConfirmed
            if (pinConfirmed) {
                enroll(context, createdPin)
            }
        }
    }

    private fun enroll(context: Context, password: String) = viewModelScope.launch {
        val currentChallenge = challenge.value ?: return@launch
        val result =
            repository.completeChallenge(EnrollmentCompleteRequest(currentChallenge, password))
        when (result) {
            is ChallengeCompleteResult.Failure -> {
                errorData.postValue(ErrorData(result.failure.title, result.failure.message))
            }
            ChallengeCompleteResult.Success -> {
                promptBiometric.value =
                    context.biometricUsable() && challenge.value?.identity?.biometricOfferUpgrade == true
            }
        }
    }

    fun handleBackNavigation(closePinSetupFlow: () -> Unit) {
        val currentStep = pinStep.value ?: PinStep.PinCreate
        if (currentStep is PinStep.PinCreate) {
            closePinSetupFlow()
        } else {
            isPinInvalid.value = false
            pinCreate.value = ""
            pinConfirm.value = ""
            promptBiometric.value = false
            pinStep.value = PinStep.PinCreate
            promptBiometric.value = null
        }
    }

    fun upgradeBiometric() = viewModelScope.launch {
        promptBiometric.postValue(null)
        challenge.value?.let { challenge ->
            val pin = pinCreate.value ?: ""
            challenge.identity.let {
                repository.upgradeBiometric(it, challenge.identityProvider, pin)
            }
        }
    }

    fun stopOfferBiometric() = viewModelScope.launch {
        promptBiometric.postValue(null)
        challenge.value?.identity?.let {
            repository.stopOfferBiometric(it)
        }
    }

    fun dismissError() {
        errorData.value = null
    }
}