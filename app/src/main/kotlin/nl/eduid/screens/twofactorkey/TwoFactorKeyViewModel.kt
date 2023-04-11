package nl.eduid.screens.twofactorkey

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TwoFactorKeyViewModel @Inject constructor(): ViewModel()  {

    val twoFaInfo = MutableLiveData<List<TwoFactorData>>()

    init {
        viewModelScope.launch {
            val twoFaDetails = listOf("","")
            twoFaInfo.postValue(convertToUiData(twoFaDetails))
        }
    }

    private fun convertToUiData(twoFaDetails: List<String>): List<TwoFactorData> {
        return twoFaDetails.map {
            TwoFactorData(
                uniqueKey = "98665f33-c43f-4f5a- 8a89-a350a10e3c36",
                title = "René v. Hamersdonkveer",
                subtitle = "acc.edu.nl",
                account = "René van Hamersdonksveer",
                biometricFlag = false,
            )
        }
    }
}