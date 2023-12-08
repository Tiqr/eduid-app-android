package nl.eduid.screens.firsttimedialog

import android.content.Intent
import android.net.Uri
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
import nl.eduid.di.model.UnauthorizedException
import javax.inject.Inject

@HiltViewModel
class LinkAccountViewModel @Inject constructor(private val assistant: DataAssistant) : ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }

    fun requestLinkUrl() = viewModelScope.launch {
        uiState = uiState.copy(inProgress = true, linkUrl = null)
        try {
            val response = assistant.getStartLinkAccount()
            if (response != null) {
                uiState =
                    uiState.copy(linkUrl = createLaunchIntent(response), inProgress = false)
            } else {
                uiState =
                    uiState.copy(
                        inProgress = false, errorData = ErrorData(
                            titleId = R.string.Generic_RequestError_Title_COPY,
                            messageId = R.string.ResponseErrors_GeneralRequestError_COPY
                        )
                    )
            }
        } catch (e: UnauthorizedException) {
            uiState = uiState.copy(
                inProgress = false, errorData = ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = R.string.ResponseErrors_UnauthorizedText_COPY
                )
            )
        }
    }

    private fun createLaunchIntent(url: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        return intent
    }
}
