package nl.eduid.screens.start

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.di.api.EduIdApi
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WelcomeStartViewModel @Inject constructor(private val eduIdApi: EduIdApi) : ViewModel() {
    val uiState = MutableLiveData(UiState())

    init {
        viewModelScope.launch {
            uiState.postValue(uiState.value?.copy(isLoading = true))
            try {
                val userDetails = eduIdApi.getUserDetails().body()
                val isLinked = userDetails?.linkedAccounts?.isNotEmpty() ?: false
                uiState.postValue(
                    uiState.value?.copy(
                        isLoading = false,
                        isAccountLinked = isLinked
                    )
                )
            } catch (e: Exception) {
                Timber.e(
                    e,
                    "Failed to retrieve the user details, cannot know if the account is already linked to an institution."
                )
                uiState.postValue(
                    uiState.value?.copy(
                        isLoading = false,
                        isAccountLinked = false
                    )
                )
            }
        }
    }
}