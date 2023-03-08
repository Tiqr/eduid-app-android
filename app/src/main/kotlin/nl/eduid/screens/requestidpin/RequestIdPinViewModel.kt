package nl.eduid.screens.requestidpin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestIdPinViewModel @Inject constructor() : ViewModel() {
    val pinValid = MutableLiveData<Boolean?>(null)

    fun isPinValid(pin: String) = viewModelScope.launch {
        delay(1000L)
        pinValid.postValue(true)
    }
}