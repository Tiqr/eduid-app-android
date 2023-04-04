package nl.eduid.screens.dataactivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.di.model.UserDetails
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import javax.inject.Inject

@HiltViewModel
class DataAndActivityViewModel @Inject constructor(private val repository: PersonalInfoRepository) :
    ViewModel() {
    val dataAndActivity = MutableLiveData<DataAndActivityData>()

    init {
        viewModelScope.launch {
            val userDetails = repository.getUserDetails()
            if (userDetails != null) {
                val uiData = convertToUiData(userDetails)
                dataAndActivity.postValue(uiData)
            }
        }
    }

    private fun convertToUiData(userDetails: UserDetails): DataAndActivityData {
        val providers = userDetails.eduIdPerServiceProvider.values.map {
            DataAndActivityData.Companion.Provider(
                providerName = it.serviceName,
                createdStamp = it.createdAt,
                firstLoginStamp = it.createdAt,
                uniqueId = it.value,
                providerLogoUrl = it.serviceLogoUrl,

            )
        }

        return DataAndActivityData(
            providerList = providers
        )
    }
}