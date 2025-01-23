package nl.eduid.screens.firsttimedialog

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.annotatedStringWithBoldParts
import nl.eduid.ui.theme.AlertWarningBackground
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun FirstTimeDialogRoute(viewModel: LinkAccountViewModel, skipThis: () -> Unit) {
    var isGettingLinkUrl by rememberSaveable { mutableStateOf(false) }
    var isLinkingStarted by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(contract = LinkAccountContract(), onResult = { _ ->
        if (isLinkingStarted) {
            // This part is called when the user came back to the app without linking anything
            // (otherwise we would go via a deeplink to the success / error screen).
            // In this case we do nothing. The user can choose to link again, or just press on the skip button.
            isLinkingStarted = false
        }
    })

    if (isGettingLinkUrl && viewModel.uiState.haveValidLinkIntent()) {
        LaunchedEffect(key1 = viewModel) {
            viewModel.uiState.linkUrl?.let { intent ->
                isGettingLinkUrl = false
                launcher.launch(intent)
                isLinkingStarted = true
            }
        }
    }
    viewModel.uiState.errorData?.let {
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = it.title(context),
            explanation = it.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = viewModel::dismissError,
        )
    }

    FirstTimeDialogScreen(
        goToAccountLinked = {
            isGettingLinkUrl = true
            viewModel.requestLinkUrl()
        },
        skipThis = skipThis,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstTimeDialogScreen(goToAccountLinked: () -> Unit, skipThis: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { CloseTopBarNoTitle(skipThis, scrollBehavior) }) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(paddingValues = paddingValues),
        ) {
            Text(
                text = stringResource(R.string.CreateEduID_FirstTimeDialog_MainTextTitle_FirstPart_COPY),
                style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.CreateEduID_FirstTimeDialog_MainTextTitle_SecondPart_COPY),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )

            Spacer(Modifier.height(40.dp))

            Box(
                Modifier
                    .background(color = AlertWarningBackground)
                    .fillMaxSize()
            ) {
                Column(modifier = Modifier.padding(vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = annotatedStringWithBoldParts(
                            stringResource(id = R.string.CreateEduID_FirstTimeDialog_MainText_COPY),
                            stringResource(id = R.string.CreateEduID_FirstTimeDialog_MainTextFirstBoldPart_COPY),
                            stringResource(id = R.string.CreateEduID_FirstTimeDialog_MainTextSecondBoldPart_COPY),
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    )
                    InfoAddedToEduId(R.string.CreateEduID_FirstTimeDialog_MainTextPoint1_COPY)
                    InfoAddedToEduId(R.string.CreateEduID_FirstTimeDialog_MainTextPoint2_COPY)
                    InfoAddedToEduId(R.string.CreateEduID_FirstTimeDialog_MainTextPoint3_COPY)
                }
            }
            Column(
                modifier = Modifier
                    .padding(24.dp),
            ) {
                Spacer(Modifier.height(40.dp))
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = annotatedStringWithBoldParts(
                        stringResource(id = R.string.CreateEduID_FirstTimeDialog_AddInformationText_COPY),
                        stringResource(id = R.string.CreateEduID_FirstTimeDialog_AddInformationBoldPart_COPY),
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                PrimaryButton(
                    text = stringResource(R.string.CreateEduID_FirstTimeDialog_ConnectButtonTitle_COPY),
                    onClick = goToAccountLinked,
                    modifier = Modifier.fillMaxWidth(),
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
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CloseTopBarNoTitle(
    skipThis: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) = CenterAlignedTopAppBar(
    title = {},
    actions = {
        Image(
            painter = painterResource(R.drawable.close_x_icon),
            contentDescription = "",
            modifier = Modifier
                .size(width = 48.dp, height = 48.dp)
                .clickable {
                    skipThis()
                },
        )
    },
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent),
    scrollBehavior = scrollBehavior,
    modifier = Modifier.padding(horizontal = 30.dp),
)

@Composable
private fun InfoAddedToEduId(labelResId: Int) = Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(start = 48.dp, end = 24.dp),
    horizontalArrangement = Arrangement.Start,
) {
    Text(
        text = "• ",
        style = MaterialTheme.typography.bodyLarge,
    )
    Text(
        text = stringResource(labelResId),
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Preview(device = Devices.NEXUS_5)
@Composable
private fun PreviewFirstTimeDialogScreen() = EduidAppAndroidTheme {
    FirstTimeDialogScreen({}, {})
}