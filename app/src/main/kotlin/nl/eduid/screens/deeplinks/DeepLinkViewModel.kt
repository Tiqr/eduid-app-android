package nl.eduid.screens.deeplinks

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import nl.eduid.BaseViewModel
import nl.eduid.CheckRecovery
import nl.eduid.R
import org.tiqr.data.model.Challenge
import org.tiqr.data.model.ChallengeParseFailure
import org.tiqr.data.model.ChallengeParseResult
import org.tiqr.data.model.EnrollmentChallenge
import org.tiqr.data.model.ParseFailure
import org.tiqr.data.repository.AuthenticationRepository
import org.tiqr.data.repository.EnrollmentRepository
import org.tiqr.data.repository.NotificationCacheRepository
import javax.inject.Inject

@HiltViewModel
class DeepLinkViewModel @Inject constructor(
    private val resources: Resources,
    private val checkRecovery: CheckRecovery,
    private val enroll: EnrollmentRepository,
    private val auth: AuthenticationRepository,
    private val notificationCacheRepository: NotificationCacheRepository,
    moshi: Moshi
) : BaseViewModel(moshi) {

    suspend fun parseChallenge(rawChallenge: String): ChallengeParseResult<*, ChallengeParseFailure> =
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

    fun clearLastNotificationChallenge() {
        notificationCacheRepository.clearLastNotificationChallenge()
    }

    fun checkIfQrEnrolment(challenge: Challenge) {
        checkRecovery.isQrEnrollment = challenge is EnrollmentChallenge
    }
}