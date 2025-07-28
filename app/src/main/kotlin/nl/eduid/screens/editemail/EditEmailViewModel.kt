package nl.eduid.screens.editemail

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
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.model.EMAIL_DOMAIN_FORBIDDEN
import nl.eduid.di.model.FAIL_EMAIL_IN_USE
import javax.inject.Inject

@HiltViewModel
class EditEmailViewModel @Inject constructor(
    private val dataAssistant: DataAssistant,
) : ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    fun onEmailChange(newValue: String) {
        uiState = uiState.copy(email = newValue, isEmailValid = true)
    }

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }

    fun requestEmailChangeClicked(newEmail: String) {
        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(uiState.email.trim()).matches()
        if (isEmailValid) {
            viewModelScope.launch {
                try {
                    uiState = uiState.copy(inProgress = true, oneTimeCodeRequested = null)
                    val response = dataAssistant.changeEmail(newEmail)
                    if (response in 200..299) {
                        uiState = uiState.copy(oneTimeCodeRequested = Unit)
                    } else {
                        val newData = when (response) {
                            FAIL_EMAIL_IN_USE -> {
                                uiState.copy(
                                    inProgress = false, errorData = ErrorData(
                                        titleId = R.string.ResponseErrors_EmailInUse_Title_COPY,
                                        messageId = R.string.ResponseErrors_EmailInUse_Description_COPY,
                                        messageArg = uiState.email
                                    )
                                )
                            }

                            EMAIL_DOMAIN_FORBIDDEN -> {
                                uiState.copy(
                                    inProgress = false, errorData = ErrorData(
                                        titleId = R.string.ResponseErrors_EmailDomainForbidden_Title_COPY,
                                        messageId = R.string.ResponseErrors_EmailDomainForbidden_Description_COPY,
                                        messageArg = uiState.email
                                    )
                                )
                            }

                            else -> {
                                uiState.copy(
                                    inProgress = false, errorData = ErrorData(
                                        titleId = R.string.Generic_RequestError_Title_COPY,
                                        messageId = R.string.Generic_RequestError_Description_COPY,
                                        messageArg = uiState.email
                                    )
                                )
                            }
                        }
                        uiState = newData
                    }
                } catch (e: Exception) {
                    uiState = uiState.copy(
                        inProgress = false, errorData = ErrorData(
                            titleId = R.string.ResponseErrors_PasswordUpdateError_COPY,
                            messageId = R.string.Generic_RequestError_Description_COPY,
                            messageArg = e.message ?: e.javaClass.simpleName,
                        )
                    )
                }
            }
        } else {
            uiState = uiState.copy(isEmailValid = false)
        }
    }

    fun confirmEmailChange(confirmHash: String) {
        uiState = uiState.copy(inProgress = true, errorData = null, oneTimeCodeRequested = null)
        viewModelScope.launch {
            try {
                dataAssistant.confirmEmail(confirmHash)
                uiState = uiState.copy(updateCompleted = Unit)
            } catch (ex: Exception) {
                uiState = uiState.copy(
                    inProgress = false,
                    errorData = ErrorData(
                        titleId = R.string.Generic_RequestError_Title_COPY,
                        messageId = R.string.Email_UpdateError_COPY,
                        messageArg = ex.message ?: ex.javaClass.simpleName,
                    )
                )
            }
        }
    }
}

data class UiState(
    val email: String = "",
    val isEmailValid: Boolean = true,
    val inProgress: Boolean = false,
    val errorData: ErrorData? = null,
    val oneTimeCodeRequested: Unit? = null,
    val updateCompleted: Unit? = null
)