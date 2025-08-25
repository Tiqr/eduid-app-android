package nl.eduid.screens.biometric

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.BaseViewModel
import nl.eduid.CheckRecovery
import nl.eduid.graphs.WithChallenge
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import org.tiqr.data.model.AuthenticationChallenge
import org.tiqr.data.model.Challenge
import org.tiqr.data.model.EnrollmentChallenge
import org.tiqr.data.repository.EnrollmentRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EnableBiometricViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    moshi: Moshi,
    private val repository: EnrollmentRepository,
    private val checkRecovery: CheckRecovery
) : BaseViewModel(moshi = moshi) {
    private val challenge: Challenge?
    private val pin: String

    var shouldAskForRecovery: Boolean? by mutableStateOf(null)
        private set

    init {
        val isEnrolment = savedStateHandle.get<Boolean>(WithChallenge.isEnrolmentArg) ?: true
        val challengeArg = savedStateHandle.get<String>(WithChallenge.challengeArg) ?: ""
        val decoded = try {
            Uri.decode(challengeArg)
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
        shouldAskForRecovery = checkRecovery.shouldAppDoRecovery()
    }

    fun stopOfferBiometric() = viewModelScope.launch {
        challenge?.identity?.let {
            repository.stopOfferBiometric(it)
        }
        shouldAskForRecovery = checkRecovery.shouldAppDoRecovery()
    }
}