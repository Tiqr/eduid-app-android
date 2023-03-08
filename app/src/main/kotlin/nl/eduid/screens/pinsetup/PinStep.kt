package nl.eduid.screens.pinsetup

sealed class PinStep {
    object PinCreate : PinStep()
    object PinConfirm : PinStep()
}