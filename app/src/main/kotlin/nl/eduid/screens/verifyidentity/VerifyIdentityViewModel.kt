package nl.eduid.screens.verifyidentity

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.assist.UnauthorizedException
import nl.eduid.di.model.IdpScoping
import nl.eduid.graphs.VerifyIdentityRoute
import javax.inject.Inject

@HiltViewModel
class VerifyIdentityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val assistant: DataAssistant
) : ViewModel() {

    private enum class LinkType {
        INSTITUTION,
        EIDAS
    }

    val isLinkedAccount: Boolean = savedStateHandle[VerifyIdentityRoute.isLinkedAccount] ?: false

    var uiState by mutableStateOf(VerifyIdentityData(isLoading = false))
        private set

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }

    private suspend fun requestLink(linkType: LinkType)  {
        uiState = uiState.copy(isLoading = true, launchIntent = null)
        try {
            val response = if (linkType == LinkType.INSTITUTION) {
                assistant.getStartLinkAccount()
            } else {
                assistant.getExternalAccountLinkResult(IdpScoping.EHERKENNING, null)
            }
            if (response != null) {
                uiState =
                    uiState.copy(launchIntent = createLaunchIntent(response), isLoading = false)
            } else {
                uiState =
                    uiState.copy(
                        isLoading = false, errorData = ErrorData(
                            titleId = R.string.Generic_RequestError_Title_COPY,
                            messageId = R.string.ResponseErrors_GeneralRequestError_COPY
                        )
                    )
            }
        } catch (e: UnauthorizedException) {
            uiState = uiState.copy(
                isLoading = false, errorData = ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = R.string.ResponseErrors_UnauthorizedText_COPY
                )
            )
        }
    }

    fun requestInstitutionLink() = viewModelScope.launch {
        requestLink(LinkType.INSTITUTION)
    }


    fun requestEidasLink() = viewModelScope.launch {
        requestLink(LinkType.EIDAS)
    }

    fun expandMoreOptions() {
        uiState = uiState.copy(moreOptionsExpanded = true)
    }

    private fun createLaunchIntent(url: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        return intent
    }

    fun clearLaunchIntent() {
        uiState = uiState.copy(launchIntent = null)
    }
}