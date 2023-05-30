package nl.eduid.screens.pinsetup

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar

@Composable
fun RegistrationPinSetupScreen(
    viewModel: RegistrationPinSetupViewModel,
    closePinSetupFlow: () -> Unit,
    goToNextStep: (NextStep) -> Unit,
    promptAuth: () -> Unit,
) {
    BackHandler { viewModel.handleBackNavigation(closePinSetupFlow) }
    //Because the same screen is being used for creating the PIN as well as confirming the PIN
    //we're using this dispatcher to ensure we can navigate back between the 2 stages correctly
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    EduIdTopAppBar(
        onBackClicked = dispatcher::onBackPressed,
    ) {
        val isAuthorized by viewModel.isAuthorized.observeAsState(initial = null)

        RegistrationPinSetupContent(
            uiState = viewModel.uiState,
            isAuthorized = isAuthorized,
            padding = it,
            goToNextStep = goToNextStep,
            promptAuth = promptAuth,
            viewModel = viewModel,
        )
    }
}

@Composable
private fun RegistrationPinSetupContent(
    uiState: UiState,
    isAuthorized: Boolean? = null,
    padding: PaddingValues = PaddingValues(),
    goToNextStep: (NextStep) -> Unit = {},
    promptAuth: () -> Unit = {},
    viewModel: RegistrationPinSetupViewModel,
) {
    var enrollmentInProgress by rememberSaveable { mutableStateOf(false) }
    var authInProgress by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
//    Disabling automatically prompting authentication for EDURED-102:
//    When user will do only a TIQR enrollment for an existing account
//    they must complete the recovery setup on web.

//    if (enrollmentInProgress && isAuthorized != null) {
//        val currentPromptAuth by rememberUpdatedState(promptAuth)
//        if (isAuthorized == false) {
//            LaunchedEffect(owner) {
//                authInProgress = true
//                enrollmentInProgress = false
//                currentPromptAuth()
//            }
//        }
//    }
    if (uiState.errorData != null) {
        AlertDialogWithSingleButton(title = uiState.errorData.title(context),
            explanation = uiState.errorData.message(context),
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = {
                viewModel.dismissError()
                enrollmentInProgress = false
            })
    }
    if (uiState.isPinInvalid) {
        LaunchedEffect(owner) {
            enrollmentInProgress = false
            authInProgress = false
        }
    }

    if ((enrollmentInProgress || authInProgress) && uiState.nextStep != null) {
        val currentGoToNextStep by rememberUpdatedState(goToNextStep)
        LaunchedEffect(owner) {
            enrollmentInProgress = false
            authInProgress = false
            currentGoToNextStep(uiState.nextStep)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
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
                enrollmentInProgress = uiState.pinStep == PinStep.PinConfirm
            },
            isProcessing = uiState.isProcessing
        )
    }
}