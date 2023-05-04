package nl.eduid.screens.dataactivity

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
import nl.eduid.di.model.UserDetails
import javax.inject.Inject

@HiltViewModel
class DataAndActivityViewModel @Inject constructor(private val assistant: DataAssistant) :
    ViewModel() {
    var uiState: UiState by mutableStateOf(UiState())
        private set

    init {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorData = null)
            try {
                val userDetails = assistant.getErringUserDetails()
                uiState = if (userDetails != null) {
                    val uiData = convertToUiData(userDetails)
                    uiState.copy(isLoading = false, errorData = null, data = uiData)
                } else {
                    uiState.copy(
                        isLoading = false, errorData = ErrorData(
                            titleId = R.string.err_title_load_fail,
                            messageId = R.string.err_msg_data_history_fail
                        )
                    )
                }
            } catch (e: UnauthorizedException) {
                uiState = uiState.copy(
                    isLoading = false, errorData = ErrorData(
                        titleId = R.string.err_title_load_fail,
                        messageId = R.string.err_msg_unauthorized_request_fail
                    )
                )
            }
        }
    }

    fun clearErrorData() {
        uiState = uiState.copy(errorData = null)
    }

    fun removeService(service: String?) = viewModelScope.launch {
        val serviceId = service ?: return@launch
        uiState = UiState(isLoading = true, errorData = null)
        try {
            val userDetails = assistant.removeService(serviceId)
            uiState = if (userDetails != null) {
                val uiData = convertToUiData(userDetails)
                UiState(
                    isLoading = false, errorData = null, data = uiData, isComplete = Unit
                )
            } else {
                UiState(
                    isLoading = false,
                    errorData = ErrorData(
                        titleId = R.string.err_title_load_fail,
                        messageId = R.string.err_msg_data_history_fail
                    ),
                )
            }
        } catch (e: UnauthorizedException) {
            uiState = uiState.copy(
                isLoading = false, errorData = ErrorData(
                    titleId = R.string.err_title_request_fail,
                    messageId = R.string.err_msg_unauthorized_request_fail
                )
            )
        }
    }

    fun goToDeleteService(provider: ServiceProvider) {
        uiState = uiState.copy(deleteService = provider)
    }

    fun cancelDeleteService() {
        uiState = uiState.copy(deleteService = null)
    }

    fun handleBackNavigation(goBack: () -> Unit) {
        val isDeletingService = uiState.deleteService != null
        if (isDeletingService) {
            cancelDeleteService()
        } else {
            goBack()
        }
    }

    private fun convertToUiData(userDetails: UserDetails): List<ServiceProvider> =
        userDetails.eduIdPerServiceProvider.values.map { service ->
            ServiceProvider(
                providerName = service.serviceName,
                createdStamp = service.createdAt,
                firstLoginStamp = service.createdAt,
                uniqueId = service.value,
                serviceProviderEntityId = service.serviceProviderEntityId,
                providerLogoUrl = service.serviceLogoUrl,
            )
        }
}