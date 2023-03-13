package nl.eduid.screens.firsttimedialog

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.theme.AlertWarningBackground
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstTimeDialogScreen(
    viewModel: LinkAccountViewModel,
    goToAccountLinked: () -> Unit,
    skipThis: () -> Unit,
) = Scaffold(modifier = Modifier.systemBarsPadding(), topBar = {
    CenterAlignedTopAppBar(
        title = {},
        actions = {
            Image(painter = painterResource(R.drawable.close_x_icon),
                contentDescription = "",
                modifier = Modifier
                    .size(width = 48.dp, height = 48.dp)
                    .clickable {
                        skipThis()
                    })
        },
        modifier = Modifier.padding(horizontal = 30.dp),
    )
}) { paddingValues ->
    val uiState by viewModel.uiState.observeAsState(UiState())
    var isGettingLinkUrl by rememberSaveable { mutableStateOf(false) }
    var isLinkingStarted by rememberSaveable { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(contract = LinkAccountContract(), onResult = { intent ->
            if (isLinkingStarted) {
                isLinkingStarted = false
                goToAccountLinked()
            }
        })

    if (isGettingLinkUrl && uiState.haveValidLinkIntent()) {
        LaunchedEffect(key1 = viewModel) {
            isGettingLinkUrl = false
            launcher.launch(uiState.linkUrl)
            isLinkingStarted = true
        }
    }

    FirstTimeDialogContent(
        uiState = uiState, paddingValues = paddingValues, onClick = {
            isGettingLinkUrl = true
            viewModel.requestLinkUrl()
        }, skipThis = skipThis, dismissError = viewModel::dismissError
    )
}

@Composable
private fun FirstTimeDialogContent(
    uiState: UiState,
    paddingValues: PaddingValues = PaddingValues(),
    onClick: () -> Unit = {},
    skipThis: () -> Unit = {},
    dismissError: () -> Unit = {},
) {
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
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.first_time_title),
                style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(R.string.first_time_subtitle),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextGreen, textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(40.dp))

            val text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringResource(R.string.first_time_description01))
                }
                append(" ")
                append(stringResource(R.string.first_time_description02))
                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringResource(R.string.first_time_description03))
                }
            }

            Box(Modifier.background(color = AlertWarningBackground)) {
                Column {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = text,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = stringResource(R.string.first_time_required),
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp)
        ) {
            val text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringResource(R.string.first_time_footer01))
                }
                append(" ")
                append(stringResource(R.string.first_time_footer02))
            }
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = text,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            PrimaryButton(
                text = stringResource(R.string.first_time_button_connect),
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            SecondaryButton(
                text = stringResource(R.string.first_time_button_skip),
                onClick = skipThis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Preview()
@Composable
private fun PreviewFirstTimeDialogScreen() {
    EduidAppAndroidTheme {
        FirstTimeDialogContent(UiState())
    }
}