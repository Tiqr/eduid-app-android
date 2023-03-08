package nl.eduid.screens.oauth

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.ScaffoldWithTopBarBackButton

@Composable
fun OAuthScreen(
    viewModel: OAuthViewModel,
    continueWith: () -> Unit,
    onBackPressed: () -> Unit,
) = ScaffoldWithTopBarBackButton(
    onBackClicked = onBackPressed,
) {
    val uiState by viewModel.uiState.observeAsState(UiState(OAuthStep.Loading))
    var isAuthorizationLaunched by rememberSaveable { mutableStateOf(false) }
    var isFetchingToken by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = OAuthContract(), onResult = { intent ->
            isAuthorizationLaunched = false
            viewModel.continueWithFetchToken(intent)
            isFetchingToken = true
        })

    if (isFetchingToken && uiState.oauthStep is OAuthStep.Authorized) {
        LaunchedEffect(viewModel) {
            isFetchingToken = false
            continueWith()
        }
    }
    OAuthContent(
        uiState = uiState,
        isAuthorizationLaunched = isAuthorizationLaunched,
        launchAuthorization = { intentAvailable ->
            isAuthorizationLaunched = true
            launcher.launch(intentAvailable)
        },
        dismissError = viewModel::dismissError,
        onRetry = { viewModel.prepareAppAuth(context) },
    )
}

@Composable
private fun OAuthContent(
    uiState: UiState,
    isAuthorizationLaunched: Boolean,
    launchAuthorization: (Intent) -> Unit,
    dismissError: () -> Unit,
    onRetry: () -> Unit,
) {
    if (uiState.error != null) {
        AlertDialogWithSingleButton(
            title = uiState.error.title,
            explanation = uiState.error.message,
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = dismissError
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = stringResource(R.string.oauth_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.oauth_description),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            if (uiState.oauthStep.isProcessing) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (uiState.oauthStep is OAuthStep.Initialized && !isAuthorizationLaunched) {
                LaunchedEffect(Unit) {
                    launchAuthorization(uiState.oauthStep.intent)
                }
            }
        }
        if (uiState.oauthStep is OAuthStep.Error) {
            PrimaryButton(
                text = stringResource(R.string.button_retry),
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(40.dp))
        }

    }
}