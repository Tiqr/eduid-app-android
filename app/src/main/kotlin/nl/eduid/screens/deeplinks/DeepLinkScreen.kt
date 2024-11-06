package nl.eduid.screens.deeplinks

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nl.eduid.ErrorData
import nl.eduid.R
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
            viewModel.clearLastNotificationChallenge()
            if (parseResult is ChallengeParseResult.Failure) {
                errorData = ErrorData(parseResult.failure.title, parseResult.failure.message)
            } else if (parseResult is ChallengeParseResult.Success) {
                currentGoToNext(parseResult.value)
            }
        }
    }

    DeepLinkContent(
        inProgress = isParsingLinkPayload,
        errorData = errorData,
        context = context,
        paddingValues = it,
    ) {
        errorData = null
    }
}

@Composable
private fun DeepLinkContent(
    inProgress: Boolean,
    errorData: ErrorData?,
    context: Context = LocalContext.current,
    paddingValues: PaddingValues = PaddingValues(),
    dismissError: () -> Unit,
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .navigationBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
) {
    if (errorData != null) {
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = dismissError
        )
    }
    Text(
        text = stringResource(R.string.Deeplink_Processing_COPY),
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