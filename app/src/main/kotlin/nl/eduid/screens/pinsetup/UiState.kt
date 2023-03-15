package nl.eduid.screens.pinsetup

import nl.eduid.screens.scan.ErrorData

data class UiState(
    val pinStep: PinStep = PinStep.PinCreate,
    val pinValue: String = "",
    val pinConfirmValue: String = "",
    val isPinInvalid: Boolean = false,
    val promptBiometric: Boolean? = null,
    val nextStep: NextStep? = null,
    val errorData: ErrorData? = null,
    val isEnrolling: Boolean = false
)

sealed class NextStep {
    object PromptBiometric : NextStep()
    data class Authenticate(val immediate: Boolean = false) : NextStep()
    object Recovery : NextStep()
}