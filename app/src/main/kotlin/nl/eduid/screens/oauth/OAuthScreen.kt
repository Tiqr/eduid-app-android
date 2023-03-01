package nl.eduid.screens.oauth

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
fun OAuthScreen(viewModel: OAuthViewModel, onBackPressed: () -> Unit) =
    ScaffoldWithTopBarBackButton(
        onBackClicked = onBackPressed,
    ) {
        val isInitializing by viewModel.isProcessing.observeAsState(true)
        val isReady by viewModel.isReady.observeAsState(null)
        val errorData by viewModel.errorData.observeAsState(null)
        var isOAuthOngoing by rememberSaveable { mutableStateOf(false) }
        val launcher =
            rememberLauncherForActivityResult(contract = OAuthContract(), onResult = { intent ->
                isOAuthOngoing = false
                viewModel.continueWithFetchToken(intent)
            })
        val context = LocalContext.current

        if (errorData != null) {
            AlertDialogWithSingleButton(
                title = errorData!!.title,
                explanation = errorData!!.message,
                buttonLabel = stringResource(R.string.button_ok),
                onDismiss = viewModel::dismissError
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
                if (isInitializing) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if (isReady != null && !isOAuthOngoing) {
                    LaunchedEffect(viewModel) {
                        isOAuthOngoing = true
                        launcher.launch(viewModel)
                    }
                }
            }
            if (!isInitializing && !isOAuthOngoing) {
                PrimaryButton(
                    text = stringResource(R.string.button_retry),
                    onClick = { viewModel.prepareAppAuth(context) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(40.dp))
            }

        }
    }