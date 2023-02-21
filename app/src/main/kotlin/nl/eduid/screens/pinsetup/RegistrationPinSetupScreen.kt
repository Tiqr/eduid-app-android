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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationPinSetupScreen(
    viewModel: RegistrationPinSetupViewModel,
    enrolChallengeReceived: () -> Unit,
    closePinSetupFlow: () -> Unit,
    onRegistrationDone: () -> Unit
) {
    BackHandler { viewModel.handleBackNavigation(closePinSetupFlow) }
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
                AlertDialogWithTwoButton(
                    title = stringResource(id = org.tiqr.core.R.string.account_upgrade_biometric_title),
                    explanation = stringResource(id = org.tiqr.core.R.string.account_upgrade_biometric_message),
                    dismissButtonLabel = stringResource(R.string.button_cancel),
                    onDismiss = {
                        viewModel::stopOfferBiometric
                        onRegistrationDone()
                    },
                    confirmButtonLabel = stringResource(R.string.button_ok),
                    onConfirm = {
                        viewModel::upgradeBiometric
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
                viewModel.onPinChange(pin, step)
            },
            onClick = {
                viewModel.submitPin(context, pinStep)
            },
            paddingValues = paddingValues,
            isProcessing = false
        )
    }
}