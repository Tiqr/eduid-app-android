package nl.eduid.screens.firsttimedialog

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.di.api.EduIdApi
import nl.eduid.screens.scan.ErrorData
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LinkAccountViewModel @Inject constructor(private val eduIdApi: EduIdApi) : ViewModel() {

    val uiState = MutableLiveData(UiState())

    fun dismissError() {
        uiState.value = uiState.value?.copy(errorData = null)
    }

    fun requestLinkUrl() = viewModelScope.launch {
        uiState.postValue(UiState(inProgress = true, linkUrl = null))
        try {
            val response = eduIdApi.getStartLinkAccount()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                uiState.postValue(
                    UiState(
                        linkUrl = createLaunchIntent(body.url), inProgress = false
                    )
                )
            } else {
                uiState.postValue(
                    UiState(
                        inProgress = false, errorData = ErrorData(
                            "Failed to get link URL",
                            "Could not retrieve URL to link your current account"
                        )
                    )
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get link account for current user")
            uiState.postValue(
                UiState(
                    inProgress = false, errorData = ErrorData(
                        "Failed to get link URL",
                        "Could not retrieve URL to link your current account"
                    )
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
