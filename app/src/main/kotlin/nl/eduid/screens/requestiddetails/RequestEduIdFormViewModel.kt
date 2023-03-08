package nl.eduid.screens.requestiddetails

import android.util.Patterns
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.di.model.CREATE_EMAIL_SENT
import nl.eduid.di.model.EMAIL_DOMAIN_FORBIDDEN
import nl.eduid.di.model.FAIL_EMAIL_IN_USE
import nl.eduid.di.model.RequestEduIdAccount
import nl.eduid.di.repository.EduIdRepository
import nl.eduid.screens.scan.ErrorData
import javax.inject.Inject

@HiltViewModel
class RequestEduIdFormViewModel @Inject constructor(
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

    fun dismissError() {
        inputForm.value = inputForm.value?.copy(errorData = null)
    }

    fun requestNewEduIdAccount() = viewModelScope.launch {
        val inputData = inputForm.value ?: return@launch
        inputForm.postValue(inputData.copy(isProcessing = true, requestComplete = false))
        val responseStatus = eduIdRepo.requestEnroll(
            RequestEduIdAccount(
                email = inputData.email,
                givenName = inputData.firstName,
                familyName = inputData.lastName
            )
        )
        val newData = when (responseStatus) {
            CREATE_EMAIL_SENT -> {
                inputData.copy(isProcessing = false, requestComplete = true)
            }
            FAIL_EMAIL_IN_USE -> {
                inputData.copy(
                    isProcessing = false, errorData = ErrorData(
                        title = "Email in use",
                        message = "There already is an account registered for the email ${inputData.email}."
                    )
                )
            }
            EMAIL_DOMAIN_FORBIDDEN -> {
                inputData.copy(
                    isProcessing = false, errorData = ErrorData(
                        title = "Email domain forbidden",
                        message = "The email domain used in ${inputData.email} is not allowed."
                    )
                )
            }
            else -> {
                inputData.copy(
                    isProcessing = false, errorData = ErrorData(
                        title = "Unknown status",
                        message = "Could not create eduid account for email ${inputData.email}"
                    )
                )
            }
        }

        inputForm.postValue(newData)
    }
}

data class InputForm(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val termsAccepted: Boolean = false,
    val isProcessing: Boolean = false,
    val requestComplete: Boolean = false,
    val errorData: ErrorData? = null,
) {
    val emailValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    val isFormValid: Boolean
        get() = (emailValid && firstName.isNotEmpty() && lastName.isNotEmpty() && termsAccepted)
}

