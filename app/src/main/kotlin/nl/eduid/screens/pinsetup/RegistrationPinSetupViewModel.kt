package nl.eduid.screens.pinsetup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import nl.eduid.RegistrationPinSetup
import javax.inject.Inject

@HiltViewModel
class RegistrationPinSetupViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val pinStep: MutableLiveData<PinStep> = MutableLiveData(PinStep.PinCreate)
    val pinCreate = MutableLiveData("")
    val pinConfirm = MutableLiveData("")

    private val encodedEnrollChallenge: String

    init {
        encodedEnrollChallenge =
            savedStateHandle.get<String>(RegistrationPinSetup.registrationChallengeArg) ?: ""
    }



}