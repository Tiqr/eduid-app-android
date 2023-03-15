package nl.eduid.screens.homepage

import android.content.res.Resources
import androidx.lifecycle.*
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import nl.eduid.BaseViewModel
import nl.eduid.R
import nl.eduid.di.api.EduIdApi
import nl.eduid.di.repository.StorageRepository
import nl.eduid.screens.scan.ErrorData
import nl.eduid.screens.splash.SplashWaitTime
import org.tiqr.data.model.*
import org.tiqr.data.repository.AuthenticationRepository
import org.tiqr.data.repository.EnrollmentRepository
import org.tiqr.data.service.DatabaseService
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    db: DatabaseService,
    repository: StorageRepository,
    moshi: Moshi,
    private val resources: Resources,
    private val enroll: EnrollmentRepository,
    private val auth: AuthenticationRepository,
    private val eduIdApi: EduIdApi
) : BaseViewModel(moshi) {

    val haveRegisteredAccounts =
        db.identityCount().asLiveData(viewModelScope.coroutineContext).map {
            it != 0
        }
    val isAuthorizedForDataAccess = repository.isAuthorized.asLiveData()

    val knownState = MutableLiveData<Unit?>(null)
    val uiState = MutableLiveData(UiState())

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

    fun triggerPromptForAuth() {
        uiState.value = uiState.value?.copy(promptForAuth = Unit)
    }

    fun clearPromptForAuthTrigger() {
        uiState.value = uiState.value?.copy(promptForAuth = null)
    }

    fun dismissError() {
        uiState.value = uiState.value?.copy(errorData = null)
    }

    fun startEnrollment() = viewModelScope.launch {
        uiState.value = uiState.value?.copy(inProgress = true)
        try {
            val enrollResponse = eduIdApi.startEnrollment()
            val response = enrollResponse.body()
            if (enrollResponse.isSuccessful && response != null) {
                val challenge = parseChallenge(response.url)
                if (challenge is ChallengeParseResult.Success && challenge.value is EnrollmentChallenge) {
                    uiState.postValue(
                        uiState.value?.copy(
                            inProgress = false, currentChallenge = challenge.value, errorData = null
                        )
                    )

                }
            } else {
                uiState.postValue(
                    uiState.value?.copy(
                        inProgress = false, currentChallenge = null, errorData = ErrorData(
                            title = "Invalid enroll request",
                            message = "Cannot parse enroll request"
                        )
                    )
                )
            }

        } catch (e: Exception) {
            uiState.postValue(
                uiState.value?.copy(
                    inProgress = false, currentChallenge = null, errorData = ErrorData(
                        title = "Failed to start enrollment",
                        message = "Could not request enrollment. Check your connection and try again"
                    )
                )
            )
            Timber.e(e, "Failed to start enrollment on newly created account")
        }
    }

    private suspend fun parseChallenge(rawChallenge: String): ChallengeParseResult<*, ChallengeParseFailure> =
        when {
            enroll.isValidChallenge(rawChallenge) -> enroll.parseChallenge(rawChallenge)
            auth.isValidChallenge(rawChallenge) -> auth.parseChallenge(rawChallenge)
            else -> ChallengeParseResult.failure(
                ParseFailure(
                    title = resources.getString(R.string.error_qr_unknown_title),
                    message = resources.getString(R.string.error_qr_unknown)
                )
            )
        }

}