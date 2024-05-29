package nl.eduid.screens.firsttimedialog

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.annotatedStringWithBoldParts
import nl.eduid.ui.theme.AlertWarningBackground
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.ColorMain_Green_400

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
    var isGettingLinkUrl by rememberSaveable { mutableStateOf(false) }
    var isLinkingStarted by rememberSaveable { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(contract = LinkAccountContract(), onResult = { _ ->
            if (isLinkingStarted) {
                goToAccountLinked()
                isLinkingStarted = false
            }
        })

    if (isGettingLinkUrl && viewModel.uiState.haveValidLinkIntent()) {
        LaunchedEffect(key1 = viewModel) {
            isGettingLinkUrl = false
            launcher.launch(viewModel.uiState.linkUrl)
            isLinkingStarted = true
        }
    }

    FirstTimeDialogContent(
        uiState = viewModel.uiState, paddingValues = paddingValues, onClick = {
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
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = uiState.errorData.title(context),
            explanation = uiState.errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = dismissError
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.CreateEduID_FirstTimeDialog_MainTextTitle_FirstPart_COPY),
                style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )
            Text(
                text = stringResource(R.string.CreateEduID_FirstTimeDialog_MainTextTitle_SecondPart_COPY),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = ColorMain_Green_400, textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )

            Spacer(Modifier.height(40.dp))

            Box(Modifier.background(color = AlertWarningBackground)) {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = annotatedStringWithBoldParts(
                        stringResource(id = R.string.CreateEduID_FirstTimeDialog_MainText_COPY),
                        stringResource(id = R.string.CreateEduID_FirstTimeDialog_MainTextFirstBoldPart_COPY),
                        stringResource(id = R.string.CreateEduID_FirstTimeDialog_MainTextSecondBoldPart_COPY),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = annotatedStringWithBoldParts(
                    stringResource(id = R.string.CreateEduID_FirstTimeDialog_AddInformationText_COPY),
                    stringResource(id = R.string.CreateEduID_FirstTimeDialog_AddInformationBoldPart_COPY),
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            PrimaryButton(
                text = stringResource(R.string.CreateEduID_FirstTimeDialog_ConnectButtonTitle_COPY),
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            SecondaryButton(
                text = stringResource(R.string.CreateEduID_FirstTimeDialog_SkipButtonTitle_COPY),
                onClick = skipThis,
                modifier = Modifier.fillMaxWidth(),
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