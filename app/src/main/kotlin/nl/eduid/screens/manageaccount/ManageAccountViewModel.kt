package nl.eduid.screens.manageaccount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.graphs.ManageAccountRoute
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import javax.inject.Inject

@HiltViewModel
class ManageAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: PersonalInfoRepository,
) : ViewModel() {
    val dateString: String
    val inProgress = MutableLiveData(false)
    val downloadedResult = MutableLiveData<Boolean?>(null)

    init {
        val dateArg: String = savedStateHandle.get<String>(ManageAccountRoute.dateArg) ?: ""
        dateString = ManageAccountRoute.decodeDateFromBundle(dateArg)
    }

    fun downloadAccountData() = viewModelScope.launch {
        inProgress.postValue(true)
        val downloadResult = repository.downloadPersonalData()
        downloadedResult.postValue(downloadResult)
        inProgress.postValue(false)
    }

    fun downloadResultShown() {
        downloadedResult.value = null
    }
}