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

    fun onFirstNameChange(newValue: String) {
        inputForm.value = inputForm.value?.copy(firstName = newValue)
    }

    fun onLastNameChange(newValue: String) {
        inputForm.value = inputForm.value?.copy(lastName = newValue)
    }

    fun onTermsAccepted(newValue: Boolean) {
        inputForm.value = inputForm.value?.copy(termsAccepted = newValue)
    }

}

data class InputForm(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val termsAccepted: Boolean = false,
) {
    val emailValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    val firstNameValid: Boolean
        get() = firstName.length > 2

    val lastNameValid: Boolean
        get() = lastName.length > 2

    val isFormValid: Boolean
        get() = (emailValid && firstNameValid && lastNameValid && termsAccepted)
}

