package nl.eduid.screens.scan

import android.content.res.Resources
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import nl.eduid.BaseViewModel
import nl.eduid.R
import org.tiqr.data.model.ChallengeParseFailure
import org.tiqr.data.model.ChallengeParseResult
import org.tiqr.data.model.ParseFailure
import org.tiqr.data.repository.AuthenticationRepository
import org.tiqr.data.repository.EnrollmentRepository
import javax.inject.Inject

/**
 * Because of the way compose navigation works we cannot have navigation events as state without
 * clearing them. If we use the existing ScanViewModel.kt the last scan data is remembered (because
 * the VM lives within the nav graph lifecycle). As a result when clicking back button from the pin
 * create screen the flow for navigating back to the pin create screen is re-triggered. Rather than
 * forcing the existing ScanViewModel.kt to clear it's last known scan data opted for working with a
 * new ViewModel that doesn't keep a LiveData of the scan info.
 * */
@HiltViewModel
class StatelessScanViewModel @Inject constructor(
    private val resources: Resources,
    private val enroll: EnrollmentRepository,
    private val auth: AuthenticationRepository,
    moshi: Moshi
) : BaseViewModel(moshi) {

    suspend fun parseChallenge(rawChallenge: String): ChallengeParseResult<*, ChallengeParseFailure> =
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