package nl.eduid.screens.biometric

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.EnableBiometric
import org.tiqr.data.model.Challenge
import org.tiqr.data.model.EnrollmentChallenge
import org.tiqr.data.repository.EnrollmentRepository
import timber.log.Timber
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class EnableBiometricViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, moshi: Moshi, private val repository: EnrollmentRepository
) : ViewModel() {
    private val challenge: Challenge?
    private val pin: String

    init {
        val isEnrolment =
            savedStateHandle.get<Boolean>(EnableBiometric.biometricIsEnrolmentArg) ?: true
        val challengeArg = savedStateHandle.get<String>(EnableBiometric.biometricChallengeArg) ?: ""
        val decoded = try {
            URLDecoder.decode(challengeArg, Charsets.UTF_8.name())
        } catch (e: Exception) {
            ""
        }
        val adapter = if (isEnrolment) {
            moshi.adapter(EnrollmentChallenge::class.java)
        } else {
            moshi.adapter(EnrollmentChallenge::class.java)
        }
        pin = savedStateHandle.get<String>(EnableBiometric.biometricPinArg) ?: ""

        challenge = try {
            adapter.fromJson(decoded)
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse challenge")
            null
        }
    }

    fun upgradeBiometric() = viewModelScope.launch {
        challenge?.let { challenge ->
            challenge.identity?.let {
                repository.upgradeBiometric(it, challenge.identityProvider, pin)
            }
        }
    }

    fun stopOfferBiometric() = viewModelScope.launch {
        challenge?.identity?.let {
            repository.stopOfferBiometric(it)
        }
    }

}