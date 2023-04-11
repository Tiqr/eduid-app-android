package nl.eduid.screens.created

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.screens.homepage.HomePageViewModel
import nl.eduid.screens.homepage.UiState
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.util.LogCompositions
import org.tiqr.data.model.EnrollmentChallenge

@Composable
fun RequestEduIdCreatedScreen(
    justCreated: Boolean,
    viewModel: HomePageViewModel,
    goToOAuth: () -> Unit,
    goToRegistrationPinSetup: (EnrollmentChallenge) -> Unit,
) {
    EduIdTopAppBar(
        withBackIcon = false,
    ) {
        val uiState by viewModel.uiState.observeAsState(initial = UiState())

        if (!justCreated) {
            RequestEduIdFailedCreationContent()
        } else {
            RequestEduIdCreatedContent(
                uiState = uiState,
                goToOAuth = {
                    goToOAuth()
                    viewModel.clearPromptForAuthTrigger()
                },
                startEnrollment = viewModel::startEnrollmentAfterAccountCreation,
                goToRegistrationPinSetup = { challenge ->
                    goToRegistrationPinSetup(challenge)
                    viewModel.clearCurrentChallenge()
                },
                dismissError = viewModel::dismissError
            )
        }
    }
}

@Composable
private fun RequestEduIdCreatedContent(
    uiState: UiState,
    goToOAuth: () -> Unit = {},
    startEnrollment: () -> Unit = {},
    goToRegistrationPinSetup: (EnrollmentChallenge) -> Unit = { _ -> },
    dismissError: () -> Unit = {},
) = Column(modifier = Modifier.fillMaxSize()) {
    if (uiState.errorData != null) {
        AlertDialogWithSingleButton(
            title = uiState.errorData.title,
            explanation = uiState.errorData.message,
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = dismissError
        )
    }
    var waitingForVmEvent by rememberSaveable { mutableStateOf(false) }
    val owner = LocalLifecycleOwner.current
    LogCompositions(msg = "Account created.\nWait for VM event?: $waitingForVmEvent. \nUI State: $uiState")
    if (waitingForVmEvent && uiState.promptForAuth != null) {
        val currentGoToAuth by rememberUpdatedState(newValue = goToOAuth)
        LaunchedEffect(key1 = owner) {
            //Do not clear waitForVmEvent because now we need to wait for OAuth to complete
            currentGoToAuth()
        }

    }
    if (waitingForVmEvent && uiState.haveValidChallenge()) {
        val currentGoToRegistrationPinSetup by rememberUpdatedState(newValue = goToRegistrationPinSetup)
        LaunchedEffect(key1 = owner) {
            //Reset the composable UI state to avoid potential backstack navigation issues
            waitingForVmEvent = false
            currentGoToRegistrationPinSetup(uiState.currentChallenge as EnrollmentChallenge)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
    ) {
        Text(
            text = stringResource(R.string.request_id_created_title),
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if (uiState.inProgress) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            text = stringResource(R.string.request_id_created_description),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        Image(
            painter = painterResource(id = R.drawable.account_created_logo),
            contentDescription = "",
            modifier = Modifier
                .wrapContentSize()
                .align(alignment = Alignment.CenterHorizontally),
            alignment = Alignment.Center
        )
        Spacer(Modifier.height(24.dp))
    }

    PrimaryButton(
        text = stringResource(id = R.string.button_continue),
        onClick = {
            waitingForVmEvent = true
            startEnrollment()
        },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(40.dp))
}

@Composable
private fun RequestEduIdFailedCreationContent() = Column(modifier = Modifier.fillMaxSize()) {
    Text(
        text = stringResource(R.string.request_id_created_fail_title),
        style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(
        modifier = Modifier.height(16.dp)
    )
    Text(
        text = stringResource(R.string.request_id_created_fail_description),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth()
    )
}


@Preview
@Composable
private fun Preview_AccounCreationFailedScreen() {
    EduidAppAndroidTheme {
        RequestEduIdFailedCreationContent()
    }
}

@Preview
@Composable
private fun Preview_AccountCreatedScreen() {
    EduidAppAndroidTheme {
        RequestEduIdCreatedContent(
            uiState = UiState(),
        )
    }
}
