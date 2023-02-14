package nl.eduid.screens.personalinfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor() : ViewModel() {
    val personalInfo = MutableLiveData<PersonalInfo>()

    data class PersonalInfo(
        val name: String = "",
        val email: String = "",
        val role: String = "",
        val institution: String = "",
    )
}