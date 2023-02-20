package nl.eduid.screens.pinsetup

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.AlertDialogWithTwoButton
import org.tiqr.data.viewmodel.EnrollmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationPinSetupScreen(
    regViewModel: RegistrationPinSetupViewModel,
    viewModel: EnrollmentViewModel,
    enrollChallengeReceived: () -> Unit,
    closePinSetupFlow: () -> Unit,
    onRegistrationDone: () -> Unit
) {
    BackHandler { regViewModel.handleBackNavigation(closePinSetupFlow) }
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(modifier = Modifier
                .padding(top = 52.dp, bottom = 40.dp)
                .padding(horizontal = 10.dp),
                navigationIcon = {
                    IconButton(onClick = { dispatcher.onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.button_back),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(width = 53.dp, height = 53.dp)
                        )
                    }
                },
                title = {
                    Image(
                        painter = painterResource(R.drawable.logo_eduid_big),
                        contentDescription = "",
                        modifier = Modifier.size(width = 122.dp, height = 46.dp),
                        alignment = Alignment.Center
                    )
                })
        },
        modifier = Modifier.systemBarsPadding(),
    ) { paddingValues ->

        val pinStep by regViewModel.pinStep.observeAsState(PinStep.PinCreate)
        val pinValue: String by regViewModel.pinCreate.observeAsState("")
        val pinConfirmValue: String by regViewModel.pinConfirm.observeAsState("")
        val isPinInvalid: Boolean by regViewModel.isPinInvalid.observeAsState(false)
        val encodedChallenge by regViewModel.challenge.observeAsState(null)
        val promptBiometric by regViewModel.promptBiometric.observeAsState(null)
        val errorData by regViewModel.errorData.observeAsState(null)
        val context = LocalContext.current

        encodedChallenge?.let { encodedChallenge ->
            LaunchedEffect(regViewModel, encodedChallenge) {
                enrollChallengeReceived()
            }
        }
        if (errorData != null) {
            AlertDialogWithSingleButton(
                title = errorData!!.title,
                explanation = errorData!!.message,
                buttonLabel = stringResource(R.string.button_ok),
                onDismiss = regViewModel::dismissError
            )
        }

        if (promptBiometric != null) {
            if (promptBiometric == true) {
                AlertDialogWithTwoButton(
                    title = stringResource(id = org.tiqr.core.R.string.account_upgrade_biometric_title),
                    explanation = stringResource(id = org.tiqr.core.R.string.account_upgrade_biometric_message),
                    dismissButtonLabel = stringResource(R.string.button_cancel),
                    onDismiss = {
                        regViewModel::stopOfferBiometric
                        onRegistrationDone()
                    },
                    confirmButtonLabel = stringResource(R.string.button_ok),
                    onConfirm = {
                        regViewModel::upgradeBiometric
                        onRegistrationDone()
                    }
                )
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
                regViewModel.onPinChange(pin, step)
            },
            onClick = {
                regViewModel.submitPin(context, pinStep)
            },
            paddingValues = paddingValues,
            isProcessing = false
        )
    }
}