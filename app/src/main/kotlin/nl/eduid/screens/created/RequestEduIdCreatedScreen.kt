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
        var isProcessing by rememberSaveable { mutableStateOf(false) }
        var authOngoing by rememberSaveable { mutableStateOf(false) }

        if (isProcessing && uiState.promptForAuth != null) {
            val currentGoToAuth by rememberUpdatedState(newValue = goToOAuth)
            LaunchedEffect(key1 = viewModel) {
                isProcessing = false
                authOngoing = true
                currentGoToAuth()
            }

        }
        if ((isProcessing || authOngoing) && uiState.haveValidChallenge()) {
            val currentGoToRegistrationPinSetup by rememberUpdatedState(newValue = goToRegistrationPinSetup)
            LaunchedEffect(key1 = viewModel) {
                isProcessing = false
                authOngoing = false
                currentGoToRegistrationPinSetup(uiState.currentChallenge as EnrollmentChallenge)
            }
        }

        if (!justCreated) {
            RequestEduIdFailedCreationContent()
        } else {
            RequestEduIdCreatedContent(
                uiState = uiState,
                startEnrollment = {
                    isProcessing = true
                    viewModel.startEnrollment()
                },
                dismissError = viewModel::dismissError
            )
        }
    }
}

@Composable
private fun RequestEduIdCreatedContent(
    uiState: UiState,
    startEnrollment: () -> Unit = {},
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
        onClick = startEnrollment,
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
