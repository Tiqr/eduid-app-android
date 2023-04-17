package nl.eduid.screens.manageaccount

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.AlertWarningBackground
import nl.eduid.ui.theme.ButtonRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack
import nl.eduid.ui.theme.TextGreen

@Composable
fun ManageAccountScreen(
    viewModel: ManageAccountViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    goBack: () -> Unit,
    onDeleteAccountPressed: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack,
    snackbarHostState = snackbarHostState,
) {
    val inProgress by viewModel.inProgress.observeAsState(false)
    val downloadResult by viewModel.downloadedResult.observeAsState(null)
    downloadResult?.let { isOk ->
        val snackbarText = if (isOk) {
            stringResource(R.string.manage_account_result_ok)
        } else {
            stringResource(R.string.manage_account_result_fail)
        }
        LaunchedEffect(snackbarHostState, viewModel, snackbarText) {
            snackbarHostState.showSnackbar(snackbarText)
            viewModel.downloadResultShown()
        }
    }

    ManageAccountScreenContent(
        dateString = viewModel.dateString,
        inProgress = inProgress,
        onDownloadData = viewModel::downloadAccountData,
        onDeleteAccountPressed = onDeleteAccountPressed,
    )
}

@Composable
private fun ManageAccountScreenContent(
    dateString: String,
    inProgress: Boolean,
    onDownloadData: () -> Unit = {},
    onDeleteAccountPressed: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
) {
    Spacer(Modifier.height(36.dp))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
    ) {
        Text(
            text = stringResource(R.string.manage_account_title),
            style = MaterialTheme.typography.titleLarge.copy(
                color = TextGreen, textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "${stringResource(R.string.manage_account_subtitle)} $dateString",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextBlack, textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier
                .background(color = AlertWarningBackground)
                .padding(12.dp)
        ) {
            Column {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.manage_account_info_block),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        PrimaryButton(
            text = stringResource(R.string.manage_account_download_my_data),
            enabled = !inProgress,
            onClick = onDownloadData,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Button(
        shape = RoundedCornerShape(CornerSize(6.dp)),
        onClick = onDeleteAccountPressed,
        border = BorderStroke(1.dp, Color.Red),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonRed),
        modifier = Modifier
            .sizeIn(minHeight = 48.dp)
            .padding(bottom = 24.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.manage_account_delete_your_account),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = ButtonRed, fontWeight = FontWeight.SemiBold
            )
        )
    }
}


@Preview()
@Composable
private fun PreviewManageAccountScreen() {
    EduidAppAndroidTheme {
        ManageAccountScreenContent(dateString = "15 March 2004", inProgress = false) {}
    }
}