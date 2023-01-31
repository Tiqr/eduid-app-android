package nl.eduid.requestiddetails

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import dagger.hilt.android.lifecycle.HiltViewModel
import nl.eduid.di.repository.EduIdRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RequestIdDetailsViewModel @Inject constructor(
    private val eduIdRepo: EduIdRepository,
) : ViewModel() {
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

    //todo api work
    val requestNewId = MutableLiveData<UUID>()
    val challenge = requestNewId.switchMap { uuid ->
        liveData {
            eduIdRepo.requestEnroll(uuid).run {
                emit(this)
            }
        }
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

