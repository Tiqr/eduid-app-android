package nl.eduid.screens.pinsetup

import nl.eduid.screens.scan.ErrorData

data class RegistrationPinUiState(
    val pinStep: PinStep = PinStep.PinCreate,
    val pinValue: String = "",
    val pinConfirmValue: String = "",
    val isPinInvalid: Boolean = false,
    val promptBiometric: Boolean? = null,
    val errorData: ErrorData? = null,
    val isEnrolling: Boolean = false
)