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
import nl.eduid.di.api.EduIdApi
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.model.EMAIL_DOMAIN_FORBIDDEN
import nl.eduid.di.model.FAIL_EMAIL_IN_USE
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

@HiltViewModel
class EditEmailViewModel @Inject constructor(
    private val eduIdApi: EduIdApi,
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
                    uiState = uiState.copy(inProgress = true, isCompleted = null)
                    val response = dataAssistant.changeEmail(newEmail)
                    if (HttpsURLConnection.HTTP_OK == response) {
                        uiState = uiState.copy(inProgress = false, isCompleted = Unit)
                    } else {
                        val newData = when (response) {
                            FAIL_EMAIL_IN_USE -> {
                                uiState.copy(
                                    inProgress = false, errorData = ErrorData(
                                        titleId = R.string.err_title_email_in_use,
                                        messageId = R.string.err_msg_email_in_use,
                                        messageArg = uiState.email
                                    )
                                )
                            }

                            EMAIL_DOMAIN_FORBIDDEN -> {
                                uiState.copy(
                                    inProgress = false, errorData = ErrorData(
                                        titleId = R.string.err_title_email_domain_forbidden,
                                        messageId = R.string.err_msg_email_domain_forbidden,
                                        messageArg = uiState.email
                                    )
                                )
                            }

                            else -> {
                                uiState.copy(
                                    inProgress = false, errorData = ErrorData(
                                        titleId = R.string.err_title_generic_fail,
                                        messageId = R.string.err_msg_generic_unexpected_with_arg,
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
                            titleId = R.string.err_title_cannot_update_pass,
                            messageId = R.string.err_msg_generic_unexpected_with_arg,
                            messageArg = e.message ?: e.javaClass.simpleName,
                        )
                    )
                }
            }
        } else {
            uiState = uiState.copy(isEmailValid = false)
        }
    }
}

data class UiState(
    val email: String = "",
    val isEmailValid: Boolean = true,
    val inProgress: Boolean = false,
    val errorData: ErrorData? = null,
    val isCompleted: Unit? = null,
)