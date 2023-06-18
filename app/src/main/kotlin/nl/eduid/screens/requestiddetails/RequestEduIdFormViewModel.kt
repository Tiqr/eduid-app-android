package nl.eduid.screens.requestiddetails

import android.content.Context
import android.content.res.Resources
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.model.CREATE_EMAIL_SENT
import nl.eduid.di.model.EMAIL_DOMAIN_FORBIDDEN
import nl.eduid.di.model.FAIL_EMAIL_IN_USE
import nl.eduid.di.model.RequestEduIdAccount
import nl.eduid.di.repository.EduIdRepository
import nl.eduid.env.EnvironmentProvider
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class RequestEduIdFormViewModel @Inject constructor(
    private val eduIdRepo: EduIdRepository,
) : ViewModel() {
    var inputForm by mutableStateOf(InputForm())
        private set

    fun onEmailChange(newValue: String) {
        inputForm = inputForm.copy(email = newValue)
    }

    fun onFirstNameChange(newValue: String) {
        inputForm = inputForm.copy(firstName = newValue)
    }

    fun onLastNameChange(newValue: String) {
        inputForm = inputForm.copy(lastName = newValue)
    }

    fun onTermsAccepted(newValue: Boolean) {
        inputForm = inputForm.copy(termsAccepted = newValue)
    }

    fun dismissError() {
        inputForm = inputForm.copy(errorData = null)
    }

    fun requestNewEduIdAccount(context: Context) = viewModelScope.launch {
        val relyingPartClientId = getClientIdFromOAuthConfig(context.resources)
        inputForm = inputForm.copy(isProcessing = true, requestComplete = false)
        val responseStatus = eduIdRepo.requestEnroll(
            RequestEduIdAccount(
                email = inputForm.email,
                givenName = inputForm.firstName,
                familyName = inputForm.lastName,
                relyingPartClientId = relyingPartClientId
            )
        )
        val newData = when (responseStatus) {
            CREATE_EMAIL_SENT -> {
                inputForm.copy(isProcessing = false, requestComplete = true)
            }

            FAIL_EMAIL_IN_USE -> {
                inputForm.copy(
                    isProcessing = false, errorData = ErrorData(
                        titleId = R.string.err_title_email_in_use,
                        messageId = R.string.err_msg_email_in_use,
                        messageArg = inputForm.email
                    )
                )
            }

            EMAIL_DOMAIN_FORBIDDEN -> {
                inputForm.copy(
                    isProcessing = false, errorData = ErrorData(
                        titleId = R.string.err_title_email_domain_forbidden,
                        messageId = R.string.err_msg_email_domain_forbidden,
                        messageArg = inputForm.email
                    )
                )
            }

            else -> {
                inputForm.copy(
                    isProcessing = false, errorData = ErrorData(
                        titleId = R.string.err_title_auth_unexpected_fail,
                        messageId = R.string.err_msg_create_unknown_fail,
                        messageArg = inputForm.email
                    )
                )
            }
        }
        inputForm = newData
    }

    private fun getClientIdFromOAuthConfig(resources: Resources): String {
        val source =
            resources.openRawResource(EnvironmentProvider.getCurrent().authConfig).bufferedReader()
                .use { it.readText() }
        return try {
            JSONObject(source).get("client_id").toString()
        } catch (e: IOException) {
            Timber.e(e, "Failed to parse configurations")
            EnvironmentProvider.getCurrent().clientId
        }
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

