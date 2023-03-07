package nl.eduid.screens.personalinfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.BuildConfig
import nl.eduid.di.model.UserDetails
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(private val repository: PersonalInfoRepository) :
    ViewModel() {
    val personalInfo = MutableLiveData<PersonalInfo>()

    init {
        viewModelScope.launch {
            val userDetails = repository.getUserDetails()
            if (userDetails != null) {
                val uidata = convertToUiData(userDetails)
                personalInfo.postValue(uidata)
            }
        }
    }

    private fun convertToUiData(userDetails: UserDetails) =
        PersonalInfo(
            name = userDetails.givenName,
            nameProvider = userDetails.schacHomeOrganization,
            nameStatus = PersonalInfo.InfoStatus.Final,
            email = userDetails.email,
            emailProvider = "You",
            emailStatus = PersonalInfo.InfoStatus.Editable,
        )
}