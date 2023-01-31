package nl.eduid.screens.splash

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import org.tiqr.data.service.DatabaseService
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(db: DatabaseService) : ViewModel() {
    private val identityCount: Flow<Int> =
        db.identityCount().onStart { emit(value = -1) }.catch { ex ->
            Timber.w(ex, "Unable to determine identity count!")
            emit(value = 0)
        }
    val startupData = identityCount.asLiveData(viewModelScope.coroutineContext).switchMap {
        liveData {
            when (it) {
                -1 -> emit(Startup.Unknown)
                0 -> {
                    delay(400)
                    emit(Startup.RegistrationRequired)
                }
//                1 -> {
//                    delay(400)
//                    emit(Startup.ReadyToUse)
//                }
                else -> {
                    delay(400)
                    emit(Startup.AppReady)
                }
            }
        }
    }


}