package nl.eduid.screens.created

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import org.tiqr.data.model.EnrollmentChallenge

@Composable
fun RequestEduIdCreatedScreen(
    justCreated: Boolean,
    viewModel: HomePageViewModel,
    goToOAuth: () -> Unit,
    goToRegistrationPinSetup: (EnrollmentChallenge) -> Unit,
) = EduIdTopAppBar(
    withBackIcon = false,
) {
    viewModel.uiState.errorData?.let { errorData ->
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = viewModel::dismissError
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        if (!justCreated) {
            RequestEduIdFailedCreationContent()
        } else {
            RequestEduIdCreatedContent(
                uiState = viewModel.uiState,
                goToOAuth = {
                    goToOAuth()
                    viewModel.clearPromptForAuthTrigger()
                },
                startEnrollment = viewModel::startEnrollmentAfterAccountCreation
            ) { challenge ->
                goToRegistrationPinSetup(challenge)
                viewModel.clearCurrentChallenge()
            }
        }
    }
}

@Composable
private fun RequestEduIdCreatedContent(
    uiState: UiState,
    goToOAuth: () -> Unit = {},
    startEnrollment: () -> Unit = {},
    goToRegistrationPinSetup: (EnrollmentChallenge) -> Unit = { _ -> },
) = Column(
    modifier = Modifier
        .fillMaxSize(),
    verticalArrangement = Arrangement.SpaceBetween
) {
    var waitingForVmEvent by rememberSaveable { mutableStateOf(false) }
    val owner = LocalLifecycleOwner.current
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
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.CreateEduID_Created_MainTitleLabel_COPY),
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
            text = stringResource(R.string.CreateEduID_Created_MainText_COPY),
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
        text = stringResource(id = R.string.NameUpdated_Continue_COPY),
        onClick = {
            waitingForVmEvent = true
            startEnrollment()
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun RequestEduIdFailedCreationContent() =
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.CreateEduID_ErrorCreateFailed_Title_COPY),
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Text(
            text = stringResource(R.string.CreateEduID_ErrorCreateFailed_Message_COPY),
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
