package nl.eduid.screens.security

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.di.model.UserDetails
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(private val repository: PersonalInfoRepository) :
    ViewModel() {
    val securityInfo = MutableLiveData<SecurityScreenData>()

    init {
        viewModelScope.launch {
            val userDetails = repository.getUserDetails()
            if (userDetails != null) {
                val uiData = convertToUiData(userDetails)
                securityInfo.postValue(uiData)
            }
        }
    }

    private fun convertToUiData(userDetails: UserDetails): SecurityScreenData {
        return userDetails.eduIdPerServiceProvider.values.map {
            SecurityScreenData(
                twoFactorEnabled = false,
                email = userDetails.email,
                passwordEnabled = false,
            )
        }.first()
    }
}