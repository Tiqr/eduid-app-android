package nl.eduid.screens.homepage

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import nl.eduid.BaseViewModel
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.repository.StorageRepository
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import nl.eduid.screens.splash.SplashWaitTime
import org.tiqr.data.model.ChallengeParseFailure
import org.tiqr.data.model.ChallengeParseResult
import org.tiqr.data.model.EnrollmentChallenge
import org.tiqr.data.model.ParseFailure
import org.tiqr.data.repository.AuthenticationRepository
import org.tiqr.data.repository.EnrollmentRepository
import org.tiqr.data.repository.IdentityRepository
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
    private val personalRepository: PersonalInfoRepository,
    private val identityRepository: IdentityRepository,
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
        Timber.i("startEnrollmentAfterAccountCreation START")
        uiState.postValue(uiState.value?.copy(inProgress = true))
        val requireAuth = repository.isAuthorized.firstOrNull()
        if (requireAuth == false) {
            uiState.postValue(uiState.value?.copy(inProgress = false, promptForAuth = Unit))
        } else {
            startEnrollmentWithoutOAuthCheck()
        }
    }

    fun startEnrollmentAfterSignIn() = viewModelScope.launch {
        Timber.i("startEnrollmentAfterSignIn START")
        uiState.postValue(uiState.value?.copy(inProgress = true))
        val userDetails = personalRepository.getUserDetails()
        if (userDetails != null) {
            val existingTiqrKey = identityRepository.identity(userDetails.id).firstOrNull()
            if (userDetails.hasAppRegistered()) {
                if (existingTiqrKey == null) {
                    Timber.i("Cannot continue enrolment. Must first deactivate current app")
                    uiState.postValue(
                        uiState.value?.copy(
                            inProgress = false,
                            preEnrollCheck = PreEnrollCheck.DeactivateExisting
                        )
                    )
                } else {
                    Timber.i("Local enrolment was completed previously. This should not be possible: Login button is not accessible while there is a local key.")
                    uiState.postValue(
                        uiState.value?.copy(
                            inProgress = false,
                            preEnrollCheck = PreEnrollCheck.AlreadyCompleted
                        )
                    )
                }
            } else {
                if (existingTiqrKey == null) {
                    startEnrollmentWithoutOAuthCheck()
                } else {
                    uiState.postValue(
                        uiState.value?.copy(
                            inProgress = false,
                            preEnrollCheck = PreEnrollCheck.Incomplete
                        )
                    )
                    Timber.i("Local enrolment is invalid/expired. Offer to remove current key? This should not be possible: Login button is not accessible while there is a local key.")
                }
            }
        }
    }

    fun clearPreEnrollCheck() {
        Timber.e("Clearing preEnrollcheck")
        uiState.value = uiState.value?.copy(preEnrollCheck = null)
    }

    fun clearDeactivation() {
        Timber.e("Clearing deactivationFor")
        uiState.value = uiState.value?.copy(deactivateFor = null)
    }

    fun requestDeactivationCode() = viewModelScope.launch {
        Timber.i("handleDeactivationRequest START")
        uiState.postValue(uiState.value?.copy(inProgress = true))
        val userDetails = personalRepository.getUserDetails()
        val knownPhoneNumber = "*${userDetails?.registration?.phoneNumber}"
        val codeRequested = personalRepository.requestDeactivationForKnownPhone()
        if (codeRequested) {
            uiState.postValue(
                uiState.value?.copy(
                    inProgress = false,
                    deactivateFor = DeactivateFor(knownPhoneNumber)
                )
            )
        } else {
            uiState.postValue(
                uiState.value?.copy(
                    inProgress = false,
                    errorData = ErrorData(
                        "Failed to request deactivation code",
                        "Could not receive deactivation code on phone number: $knownPhoneNumber"
                    )
                )
            )
        }
    }

    private suspend fun startEnrollmentWithoutOAuthCheck() {
        Timber.i("\tstartEnrollmentWithoutOAuthCheck START")
        val response = personalRepository.startEnrollment()
        if (response != null) {
            val challenge = parseChallenge(response.url)
            if (challenge is ChallengeParseResult.Success && challenge.value is EnrollmentChallenge) {
                Timber.i("\tstartEnrollmentWithoutOAuthCheck OK END")
                uiState.postValue(
                    uiState.value?.copy(
                        inProgress = false, currentChallenge = challenge.value, errorData = null
                    )
                )

            }
        } else {
            Timber.i("\tstartEnrollmentWithoutOAuthCheck FAIL END")
            uiState.postValue(
                uiState.value?.copy(
                    inProgress = false, currentChallenge = null, errorData = ErrorData(
                        title = "Invalid enroll request", message = "Cannot parse enroll challenge"
                    )
                )
            )
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