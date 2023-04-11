package nl.eduid.screens.deeplinks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ErrorData
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.theme.findActivity
import org.tiqr.data.model.Challenge
import org.tiqr.data.model.ChallengeParseResult


@Composable
fun DeepLinkScreen(
    viewModel: DeepLinkViewModel,
    goToNext: (Challenge) -> Unit,
) = EduIdTopAppBar(
    withBackIcon = false,
) {
    var isParsingLinkPayload by rememberSaveable { mutableStateOf(false) }
    var errorData: ErrorData? by rememberSaveable { mutableStateOf(null) }

    val context = LocalContext.current
    val activity = context.findActivity()
    val dataString = activity.intent.dataString
    if (dataString != null) {
        val currentGoToNext by rememberUpdatedState(newValue = goToNext)
        LaunchedEffect(key1 = dataString) {
            isParsingLinkPayload = true
            val parseResult = viewModel.parseChallenge(dataString)
            if (parseResult is ChallengeParseResult.Failure) {
                errorData = ErrorData(parseResult.failure.title, parseResult.failure.message)
            } else if (parseResult is ChallengeParseResult.Success) {
                currentGoToNext(parseResult.value)
            }
        }
    }

    DeepLinkContent(
        inProgress = isParsingLinkPayload,
        errorData = errorData
    ) {
        errorData = null
    }
}

@Composable
private fun DeepLinkContent(
    inProgress: Boolean,
    errorData: ErrorData?,
    dismissError: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (errorData != null) {
            AlertDialogWithSingleButton(
                title = errorData.title,
                explanation = errorData.message,
                buttonLabel = stringResource(R.string.button_ok),
                onDismiss = dismissError
            )
        }
        Text(
            text = stringResource(R.string.deeplink_processing),
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if (inProgress) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}