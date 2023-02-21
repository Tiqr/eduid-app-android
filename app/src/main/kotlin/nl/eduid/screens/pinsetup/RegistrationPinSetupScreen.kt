package nl.eduid.screens.pinsetup

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.ScaffoldWithTopBarBackButton
import org.tiqr.data.model.Challenge

@Composable
fun RegistrationPinSetupScreen(
    viewModel: RegistrationPinSetupViewModel,
    enrolChallengeReceived: () -> Unit,
    closePinSetupFlow: () -> Unit,
    goToBiometricEnable: (Challenge, String) -> Unit,
    onRegistrationDone: () -> Unit
) {
    BackHandler { viewModel.handleBackNavigation(closePinSetupFlow) }
    //Because the same screen is being used for creating the PIN as well as confirming the PIN
    //we're using this dispatcher to ensure we can navigate back between the 2 stages correctly
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    ScaffoldWithTopBarBackButton(
        onBackClicked = dispatcher::onBackPressed,
    ) {

        val pinStep by viewModel.pinStep.observeAsState(PinStep.PinCreate)
        val pinValue: String by viewModel.pinCreate.observeAsState("")
        val pinConfirmValue: String by viewModel.pinConfirm.observeAsState("")
        val isPinInvalid: Boolean by viewModel.isPinInvalid.observeAsState(false)
        val encodedChallenge by viewModel.challenge.observeAsState(null)
        val promptBiometric by viewModel.promptBiometric.observeAsState(null)
        val errorData by viewModel.errorData.observeAsState(null)
        val context = LocalContext.current
        encodedChallenge?.let { challenge ->
            LaunchedEffect(viewModel, challenge) {
                enrolChallengeReceived()
            }
        }
        if (errorData != null) {
            AlertDialogWithSingleButton(
                title = errorData!!.title,
                explanation = errorData!!.message,
                buttonLabel = stringResource(R.string.button_ok),
                onDismiss = viewModel::dismissError
            )
        }

        if (promptBiometric != null) {
            if (promptBiometric == true) {
                goToBiometricEnable(encodedChallenge!!, pinValue)
            }
            if (promptBiometric == false) {
                onRegistrationDone()
            }
        }

        PinContent(
            pinCode = if (pinStep is PinStep.PinCreate) {
                pinValue
            } else {
                pinConfirmValue
            },
            pinStep = pinStep,
            isPinInvalid = isPinInvalid,
            title = if (pinStep is PinStep.PinCreate) {
                stringResource(R.string.pinsetup_title)
            } else {
                stringResource(R.string.pinsetup_confirm_title)
            },
            description = if (pinStep is PinStep.PinCreate) {
                stringResource(R.string.pinsetup_description)
            } else {
                stringResource(R.string.pinsetup_confirm_description)
            },
            label = "",
            onPinChange = { pin, step ->
                viewModel.onPinChange(pin, step)
            },
            onClick = {
                viewModel.submitPin(context, pinStep)
            },
            paddingValues = PaddingValues(),
            isProcessing = false
        )
    }
}