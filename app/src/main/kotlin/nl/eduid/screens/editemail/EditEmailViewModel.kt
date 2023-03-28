package nl.eduid.screens.editemail

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.di.model.UserDetails
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import nl.eduid.screens.requestiddetails.InputForm
import javax.inject.Inject

@HiltViewModel
class EditEmailViewModel @Inject constructor(private val repository: PersonalInfoRepository) : ViewModel() {
    val emailInput = MutableLiveData("")
    val emailValid = MutableLiveData(false)

    fun onEmailChange(newValue: String) {
        emailInput.value = newValue
        emailValid.value = Patterns.EMAIL_ADDRESS.matcher(emailInput.value?.trim() ?: "").matches()
    }
}