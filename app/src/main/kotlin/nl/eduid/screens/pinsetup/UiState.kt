package nl.eduid.screens.pinsetup

import nl.eduid.screens.scan.ErrorData
import org.tiqr.data.model.Challenge

data class UiState(
    val pinStep: PinStep = PinStep.PinCreate,
    val pinValue: String = "",
    val pinConfirmValue: String = "",
    val isPinInvalid: Boolean = false,
    val promptAuth: Boolean? = null,
    val nextStep: NextStep? = null,
    val errorData: ErrorData? = null,
    val isEnrolling: Boolean = false
)

sealed class NextStep {
    data class PromptBiometric(val challenge: Challenge, val pin: String) : NextStep()
    object Recovery : NextStep()
    object Home : NextStep()
}