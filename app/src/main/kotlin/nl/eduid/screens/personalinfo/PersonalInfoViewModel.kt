package nl.eduid.screens.personalinfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import nl.eduid.BuildConfig
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor() : ViewModel() {
    val personalInfo = MutableLiveData<PersonalInfo>()
    init {
        if (BuildConfig.DEBUG) {
            personalInfo.value = PersonalInfo.demoData()
        }
    }
}