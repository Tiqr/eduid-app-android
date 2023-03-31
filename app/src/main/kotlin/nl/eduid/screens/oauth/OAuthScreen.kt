package nl.eduid.screens.oauth

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nl.eduid.BuildConfig
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton

/**
 * The OAuth flow has 2 sources of events: the UI screen and view model events.
 * For the OAuth flow to go through it's correct stages it's required
 * From the UI the events are:
 *  - Intent result returned by the OAuth flow triggeres the fetch token stage
 *  - Retry in case of failure
 *
 *  From the view model the relevant states that can trigger an event:
 *
 *  - Intent is available for launching OAuth [OAuthStep.Initialized] and oauth is not yet launched
 *      (isAuthorizationLaunched==false [OAuthUiStages])
 *  - OAuth completed and the user is authorized [OAuthStep.Authorized] and token fetch was started
 *      (isFetchingToken == true [OAuthUiStages])
 *
 * The OAuth flow goes through 8 recompositions and plenty of state changes. Two of the recompositions
 * are happening due to the app pausing and resuming. The OAuth flow _should_ not be impacted by the
 * pause/resume of the app, because there is no guarantee the pause/resume are triggered by the
 * actual AppAuth launch and result returning.
 * */
@Composable
fun OAuthScreen(
    viewModel: OAuthViewModel,
    goToPrevious: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goToPrevious,
) {
    val uiState by viewModel.uiState.observeAsState(UiState(OAuthStep.Loading))
    var oAuthUiStages by rememberSaveable { mutableStateOf(OAuthUiStages()) }
//    Timber.w("Started OAuthScreen with state: $uiState.\n\t IsAuthorizationLaunched: ${oAuthUiStages.isAuthorizationLaunched}.\n\t IsFetchingToken: ${oAuthUiStages.isFetchingToken}")
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = OAuthContract(), onResult = { intent ->
//            Timber.e("1 - Received intent fro AppAuth")
            viewModel.continueWithFetchToken(intent)
            //Move to the next stage for fetching the token
            oAuthUiStages = OAuthUiStages(
                isAuthorizationLaunched = false, isFetchingToken = true
            )
        })

    if (oAuthUiStages.isFetchingToken && uiState.oauthStep is OAuthStep.Authorized) {
        val currentGoToPrevious by rememberUpdatedState(newValue = goToPrevious)
        LaunchedEffect(viewModel) {
            //Clear the uistages to prevent any back stack navigation issues.
            oAuthUiStages = OAuthUiStages()
            currentGoToPrevious()
        }
    }
    OAuthContent(
        uiState = uiState,
        isAuthorizationLaunched = oAuthUiStages.isAuthorizationLaunched,
        launchAuthorization = { intentAvailable ->
//            Timber.e("0 - AppAuth intent available. Launching OAuth")
            oAuthUiStages = OAuthUiStages(
                isAuthorizationLaunched = true,
            )
            viewModel.authorizationLaunched()
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
//    LogCompositions("Recomposition for OAuthContent")
    val extraInfo = if (BuildConfig.DEBUG) {
        "\n(State: $uiState. isAuthorizationLaunched: $isAuthorizationLaunched)"
    } else {
        ""
    }
    if (uiState.error != null) {
        AlertDialogWithSingleButton(
            title = uiState.error.title,
            explanation = uiState.error.message + extraInfo,
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
                LaunchedEffect(uiState.oauthStep.intent) {
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