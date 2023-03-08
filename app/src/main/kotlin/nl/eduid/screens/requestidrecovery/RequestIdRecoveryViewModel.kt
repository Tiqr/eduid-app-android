package nl.eduid.screens.requestidrecovery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RequestIdRecoveryViewModel @Inject constructor() : ViewModel() {
    val recoveryPhoneInput = MutableLiveData("")

    fun onPhoneNumberChanged(newValue: String) {
        recoveryPhoneInput.value = newValue
    }

}