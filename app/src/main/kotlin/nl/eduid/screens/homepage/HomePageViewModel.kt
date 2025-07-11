package nl.eduid.screens.homepage

import android.content.res.Resources
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import nl.eduid.CheckRecovery
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
    private val checkRecovery: CheckRecovery,
    private val personalRepository: PersonalInfoRepository,
    private val identityRepository: IdentityRepository,
) : BaseViewModel(moshi) {

    val isAuthorizedForDataAccess = repository.isAuthorized.asLiveData()
    val didDeactivateLinkedDevice = repository.didDeactivateLinkedDevice.asLiveData()

    var uiState by mutableStateOf(UiState())
        private set
    var isEnrolledState: IsEnrolled by mutableStateOf(IsEnrolled.Unknown)
        private set

    private var jwt: JWT? = null

    init {
        viewModelScope.launch {
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
            isEnrolledState = isEnrolled
        }
    }

    fun triggerPromptForAuth() {
        uiState = uiState.copy(promptForAuth = Unit)
    }

    fun clearPromptForAuthTrigger() {
        uiState = uiState.copy(promptForAuth = null)
    }

    fun clearCurrentChallenge() {
        uiState = uiState.copy(currentChallenge = null)
    }

    fun dismissError() {
        uiState = uiState.copy(inProgress = false, errorData = null)
    }

    fun startEnrollmentAfterAccountCreation() = viewModelScope.launch {
        uiState = uiState.copy(inProgress = true)
        checkRecovery.isQrEnrollment = false
        val requireAuth = repository.isAuthorized.firstOrNull()
        if (requireAuth == false) {
            uiState = uiState.copy(inProgress = false, promptForAuth = Unit)
        } else {
            startEnrollmentWithoutOAuthCheck()
        }
    }

    fun startEnrollmentAfterSignIn() = viewModelScope.launch {
        uiState = uiState.copy(inProgress = true)
        val userDetails = personalRepository.getUserDetails()
        if (userDetails != null) {
            val existingTiqrKey = identityRepository.identity(userDetails.id).firstOrNull()
            if (userDetails.hasAppRegistered()) {
                if (existingTiqrKey == null) {
                    Timber.i("Cannot continue enrolment. Must first deactivate current app")
                    uiState = uiState.copy(
                        inProgress = false, preEnrollCheck = PreEnrollCheck.DeactivateExisting
                    )
                } else {
                    Timber.i("Local enrolment was completed previously. This should not be possible: Login button is not accessible while there is a local key.")
                    uiState = uiState.copy(
                        inProgress = false, preEnrollCheck = PreEnrollCheck.AlreadyCompleted
                    )
                }
            } else {
                if (existingTiqrKey == null) {
                    startEnrollmentWithoutOAuthCheck()
                } else {
                    uiState = uiState.copy(
                        inProgress = false, preEnrollCheck = PreEnrollCheck.Incomplete
                    )
                    Timber.i("Local enrolment is invalid/expired. Offer to remove current key? This should not be possible: Login button is not accessible while there is a local key.")
                }
            }
        } else {
            Timber.i("Local enrolment was completed previously. This should not be possible: Login button is not accessible while there is a local key.")
            uiState = uiState.copy(
                inProgress = false, preEnrollCheck = PreEnrollCheck.MissingAccount
            )
        }
    }

    fun clearPreEnrollCheck() {
        uiState = uiState.copy(preEnrollCheck = null)
    }

    fun clearDeactivation() {
        uiState = uiState.copy(deactivateFor = null)
    }

    fun requestDeactivationCode() = viewModelScope.launch {
        uiState = uiState.copy(inProgress = true, preEnrollCheck = null)
        val userDetails = personalRepository.getUserDetails()
        val knownPhoneNumber = "*${userDetails?.registration?.phoneNumber}"
        val codeRequested = personalRepository.requestDeactivationForKnownPhone()
        if (codeRequested) {
            uiState = uiState.copy(
                inProgress = false, deactivateFor = DeactivateFor(knownPhoneNumber)
            )
        } else {
            uiState = uiState.copy(
                inProgress = false, errorData = ErrorData(
                    titleId = R.string.ResponseErrors_DeactivationError_Title_COPY,
                    messageId = R.string.ResponseErrors_DeactivationError_Description_COPY,
                    messageArg = knownPhoneNumber
                )
            )
        }
    }

    private suspend fun startEnrollmentWithoutOAuthCheck() {
        val response = personalRepository.startEnrollment()
        if (response != null) {
            val challenge = parseChallenge(response.url)
            if (challenge is ChallengeParseResult.Success && challenge.value is EnrollmentChallenge) {
                uiState = uiState.copy(
                    inProgress = false, currentChallenge = challenge.value, errorData = null
                )
            }
        } else {
            uiState = uiState.copy(
                inProgress = false, currentChallenge = null, errorData = ErrorData(
                    titleId = R.string.ResponseErrors_InvalidChallenge_Title_COPY,
                    messageId = R.string.ResponseErrors_InvalidChallenge_Description_COPY,
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
                    title = resources.getString(R.string.QR_UnknownErrorTitle_COPY),
                    message = resources.getString(R.string.QR_UnknownError_COPY)
                )
            )
        }
}
