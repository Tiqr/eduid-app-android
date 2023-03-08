package nl.eduid.screens.biometric

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.BaseViewModel
import nl.eduid.graphs.WithChallenge
import org.tiqr.data.model.AuthenticationChallenge
import org.tiqr.data.model.Challenge
import org.tiqr.data.model.EnrollmentChallenge
import org.tiqr.data.repository.EnrollmentRepository
import timber.log.Timber
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class EnableBiometricViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, moshi: Moshi, private val repository: EnrollmentRepository
) : BaseViewModel(moshi) {
    private val challenge: Challenge?
    private val pin: String

    init {
        val isEnrolment = savedStateHandle.get<Boolean>(WithChallenge.isEnrolmentArg) ?: true
        val challengeArg = savedStateHandle.get<String>(WithChallenge.challengeArg) ?: ""
        val decoded = try {
            URLDecoder.decode(challengeArg, Charsets.UTF_8.name())
        } catch (e: Exception) {
            ""
        }
        val adapter = if (isEnrolment) {
            moshi.adapter(EnrollmentChallenge::class.java)
        } else {
            moshi.adapter(AuthenticationChallenge::class.java)
        }
        pin = savedStateHandle.get<String>(WithChallenge.pinArg) ?: ""
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