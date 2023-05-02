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
import nl.eduid.di.api.EduIdApi
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LinkAccountViewModel @Inject constructor(private val eduIdApi: EduIdApi) : ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }

    fun requestLinkUrl() = viewModelScope.launch {
        uiState = uiState.copy(inProgress = true, linkUrl = null)
        try {
            val response = eduIdApi.getStartLinkAccount()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                uiState =
                    uiState.copy(linkUrl = createLaunchIntent(body.url), inProgress = false)
            } else {
                val argument =
                    "[${response.code()}/${response.message()}]${response.errorBody()?.string()}"
                uiState =
                    uiState.copy(
                        inProgress = false, errorData = ErrorData(
                            titleId = R.string.err_title_request_fail,
                            messageId = R.string.err_title_link_account_fail_reason,
                            messageArg = argument
                        )
                    )
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get link account for current user")
            uiState =
                uiState.copy(
                    inProgress = false, errorData = ErrorData(
                        titleId = R.string.err_title_request_fail,
                        messageId = R.string.err_title_link_account_fail
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
