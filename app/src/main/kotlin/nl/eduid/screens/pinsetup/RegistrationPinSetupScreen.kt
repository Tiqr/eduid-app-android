package nl.eduid.screens.pinsetup

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.ScaffoldWithTopBarBackButton
import org.tiqr.data.model.Challenge

@Composable
fun RegistrationPinSetupScreen(
    viewModel: RegistrationPinSetupViewModel,
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
        val context = LocalContext.current
        val uiState by viewModel.uiState.observeAsState(initial = RegistrationPinUiState())
        var validationInProgress by rememberSaveable { mutableStateOf(false) }


        if (validationInProgress && uiState.promptBiometric != null) {
            val currentGoToBiometric by rememberUpdatedState(goToBiometricEnable)
            val currentRegistrationDone by rememberUpdatedState(onRegistrationDone)
            LaunchedEffect(viewModel) {
                validationInProgress = false
                if (uiState.promptBiometric == true && viewModel.challenge != null) {
                    currentGoToBiometric(viewModel.challenge, uiState.pinValue)
                }
                if (uiState.promptBiometric == false) {
                    currentRegistrationDone()
                }
            }
        }

        if (uiState.errorData != null) {
            AlertDialogWithSingleButton(
                title = uiState.errorData!!.title,
                explanation = uiState.errorData!!.message,
                buttonLabel = stringResource(R.string.button_ok),
                onDismiss = viewModel::dismissError
            )
        }

        PinContent(
            pinCode = if (uiState.pinStep is PinStep.PinCreate) {
                uiState.pinValue
            } else {
                uiState.pinConfirmValue
            },
            pinStep = uiState.pinStep,
            isPinInvalid = uiState.isPinInvalid,
            title = if (uiState.pinStep is PinStep.PinCreate) {
                stringResource(R.string.pinsetup_title)
            } else {
                stringResource(R.string.pinsetup_confirm_title)
            },
            description = if (uiState.pinStep is PinStep.PinCreate) {
                stringResource(R.string.pinsetup_description)
            } else {
                stringResource(R.string.pinsetup_confirm_description)
            },
            label = "",
            onPinChange = { pin, step ->
                viewModel.onPinChange(pin, step)
            },
            onClick = {
                viewModel.submitPin(context, uiState.pinStep)
                validationInProgress = uiState.pinStep == PinStep.PinConfirm
            },
            paddingValues = PaddingValues(),
            isProcessing = false
        )
    }
}