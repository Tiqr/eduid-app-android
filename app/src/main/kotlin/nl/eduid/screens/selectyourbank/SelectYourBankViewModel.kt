package nl.eduid.screens.selectyourbank

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.model.IdpScoping
import nl.eduid.di.model.VerifyIssuer
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SelectYourBankViewModel @Inject constructor(
    private val assistant: DataAssistant,
    private val repository: PersonalInfoRepository,
) : ViewModel() {

    var uiState: UiState by mutableStateOf(UiState())

    fun fetchIssuerList() {
        viewModelScope.launch {
            try {
                val issuers = repository.getVerifyIssuers()
                uiState = uiState.copy(
                    isLoading = false,
                    verifyIssuerList = issuers!!
                )
            } catch (ex: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorData = ErrorData(
                        titleId = R.string.Generic_RequestError_Title_COPY,
                        messageId = R.string.ResponseErrors_GeneralRequestError_COPY,
                    )
                )
            }
        }
    }

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }

    fun linkAccountWithBankId(issuerId: String, callback: () -> Unit) {
        viewModelScope.launch {
            try {
                val result = assistant.getExternalAccountLinkResult(IdpScoping.IDIN, issuerId)
                uiState = uiState.copy(launchIntent = Intent(Intent.ACTION_VIEW, Uri.parse(result)))
                callback()
            } catch (ex: Exception) {
                Timber.w(ex, "Unable to link account with bank ID")
                callback()
                uiState = uiState.copy(
                    errorData = ErrorData(
                        titleId = R.string.Generic_RequestError_Title_COPY,
                        messageId = R.string.ResponseErrors_GeneralRequestError_COPY,
                    )
                )
            }
        }
    }

    fun clearLaunchIntent() {
        uiState = uiState.copy(launchIntent = null)
    }
}

data class UiState(
    val isLoading: Boolean = true,
    val errorData: ErrorData? = null,
    val verifyIssuerList: List<VerifyIssuer> = emptyList(),
    val launchIntent: Intent? = null
)