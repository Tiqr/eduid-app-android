package nl.eduid.requestiddetails

import android.util.Patterns
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import org.tiqr.data.service.DatabaseService
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RequestIdDetailsViewModel @Inject constructor() : ViewModel() {
    val inputForm = MutableLiveData(InputForm())

    fun onEmailChange(newValue: String) {
        inputForm.value = inputForm.value?.copy(email = newValue)
    }

}

data class InputForm(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val termsAccepted: Boolean = false,
) {
    val isFormValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

