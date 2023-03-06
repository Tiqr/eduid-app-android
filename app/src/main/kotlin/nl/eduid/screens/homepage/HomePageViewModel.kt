package nl.eduid.screens.homepage

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import nl.eduid.screens.splash.SplashWaitTime
import org.tiqr.data.service.DatabaseService
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(db: DatabaseService) : ViewModel() {
    val haveRegisteredAccounts =
        db.identityCount().asLiveData(viewModelScope.coroutineContext).map {
            it != 0
        }

    val knownState = MutableLiveData<Unit?>(null)

    init {
        viewModelScope.launch {
            val countFromDb = async {
                val totalCount = db.identityCount().firstOrNull()
                totalCount != 0
            }
            val showSplashForMinimum = async(start = CoroutineStart.LAZY) {
                delay(SplashWaitTime)
            }
            joinAll(countFromDb, showSplashForMinimum)
            knownState.postValue(Unit)
        }
    }
}