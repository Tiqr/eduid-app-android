package nl.eduid.screens.dataactivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.di.model.UserDetails
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import javax.inject.Inject

@HiltViewModel
class DataAndActivityViewModel @Inject constructor(private val repository: PersonalInfoRepository) :
    ViewModel() {
    val uiState = MutableLiveData<UiState>()

    init {
        viewModelScope.launch {
            uiState.postValue(UiState(isLoading = true, errorData = null))
            val userDetails = repository.getUserDetails()
            if (userDetails != null) {
                val uiData = convertToUiData(userDetails)
                uiState.postValue(UiState(isLoading = false, errorData = null, data = uiData))
            } else {
                uiState.postValue(
                    UiState(
                        isLoading = false, errorData = ErrorData(
                            "Failed to load data", "Could not load activity history"
                        )
                    )
                )
            }
        }
    }

    fun clearErrorData() {
        uiState.value = uiState.value?.copy(errorData = null)
    }

    fun removeService(service: String?) = viewModelScope.launch {
        val serviceId = service ?: return@launch
        uiState.postValue(UiState(isLoading = true, errorData = null))
        val userDetails = repository.removeService(serviceId)
        if (userDetails != null) {
            val uiData = convertToUiData(userDetails)
            uiState.postValue(
                UiState(
                    isLoading = false, errorData = null, data = uiData, isComplete = Unit
                )
            )
        } else {
            uiState.postValue(
                UiState(
                    isLoading = false,
                    errorData = ErrorData(
                        "Failed to load data", "Could not load activity history"
                    ),
                )
            )
        }
    }

    fun goToDeleteService(provider: ServiceProvider) {
        uiState.value = uiState.value?.copy(deleteService = provider)
    }

    fun cancelDeleteService() {
        uiState.value = uiState.value?.copy(deleteService = null)
    }

    fun handleBackNavigation(goBack: () -> Unit) {
        val isDeletingService = uiState.value?.deleteService != null
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