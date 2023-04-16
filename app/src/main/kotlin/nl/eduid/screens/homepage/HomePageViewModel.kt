package nl.eduid.screens.homepage

import android.content.res.Resources
import androidx.lifecycle.*
import com.auth0.android.jwt.JWT
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import nl.eduid.BaseViewModel
import nl.eduid.R
import nl.eduid.di.api.EduIdApi
import nl.eduid.di.repository.StorageRepository
import nl.eduid.ErrorData
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
    moshi: Moshi,
    private val resources: Resources,
    private val enroll: EnrollmentRepository,
    private val auth: AuthenticationRepository,
    private val repository: StorageRepository,
    private val eduIdApi: EduIdApi,
) : BaseViewModel(moshi) {

    val isAuthorizedForDataAccess = repository.isAuthorized.asLiveData()
    val uiState = MutableLiveData(UiState())
    private var jwt: JWT? = null

    init {
        viewModelScope.launch {
            uiState.postValue(uiState.value?.copy(isEnrolled = IsEnrolled.Unknown))
            val haveDbEntry = async {
                val totalCount = db.identityCount().firstOrNull()
                totalCount != 0
            }
            val showSplashForMinimum = async(start = CoroutineStart.LAZY) {
                delay(SplashWaitTime)
            }
            val authState = repository.authState.firstOrNull()
            authState?.idToken?.let {
                jwt = JWT(it)
            }

            joinAll(haveDbEntry, showSplashForMinimum)
            val isEnrolled = if (haveDbEntry.await()) IsEnrolled.Yes else IsEnrolled.No
            uiState.postValue(uiState.value?.copy(isEnrolled = isEnrolled))
        }
    }

    fun triggerPromptForAuth() {
        uiState.value = uiState.value?.copy(promptForAuth = Unit)
    }

    fun clearPromptForAuthTrigger() {
        uiState.value = uiState.value?.copy(promptForAuth = null)
    }

    fun clearCurrentChallenge() {
        uiState.value = uiState.value?.copy(currentChallenge = null)
    }

    fun dismissError() {
        uiState.value = uiState.value?.copy(inProgress = false, errorData = null)
    }

    fun startEnrollmentAfterAccountCreation() = viewModelScope.launch {
        uiState.postValue(uiState.value?.copy(inProgress = true))
        val requireAuth = repository.isAuthorized.firstOrNull()
        if (requireAuth == false) {
            uiState.postValue(uiState.value?.copy(inProgress = false, promptForAuth = Unit))
        } else {
            startEnrollmentWithoutOAuthCheck()
        }
    }

    fun startEnrollmentAfterSignIn() = viewModelScope.launch {
        uiState.postValue(uiState.value?.copy(inProgress = true))
        startEnrollmentWithoutOAuthCheck()
    }

    private suspend fun startEnrollmentWithoutOAuthCheck() {
        try {
            val enrollResponse = eduIdApi.startEnrollment()
            val response = enrollResponse.body()
            if (enrollResponse.isSuccessful && response != null) {
                val challenge = parseChallenge(response.url)
                if (challenge is ChallengeParseResult.Success && challenge.value is EnrollmentChallenge) {
                    uiState.postValue(
                        uiState.value?.copy(
                            inProgress = false,
                            currentChallenge = challenge.value,
                            errorData = null
                        )
                    )

                }
            } else {
                val errorMessage = enrollResponse.errorBody()?.string()
                uiState.postValue(
                    uiState.value?.copy(
                        inProgress = false, currentChallenge = null, errorData = ErrorData(
                            title = "Invalid enroll request",
                            message = errorMessage ?: "Cannot parse enroll challenge"
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