package nl.eduid.screens.manageaccount

import android.app.DownloadManager
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.AlertWarningBackground
import nl.eduid.ui.theme.ColorAlertRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.ColorGrayScaleBlack
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
    contentWindowInsets = ScaffoldDefaults.contentWindowInsets
) {
    val inProgress by viewModel.inProgress.observeAsState(false)
    val downloadResult by viewModel.downloadedResult.observeAsState(null)
    val context = LocalContext.current

    downloadResult?.let { isOk ->
        val snackbarText = if (isOk) {
            stringResource(R.string.MyAccount_DownloadSuccess_COPY)
        } else {
            stringResource(R.string.MyAccount_DownloadError_COPY)
        }
        val okAction = stringResource(R.string.MyAccount_DownloadDataButton_COPY)
        LaunchedEffect(snackbarHostState, viewModel, snackbarText) {
            val snackbarResult = snackbarHostState.showSnackbar(
                snackbarText,
                actionLabel = okAction,
                withDismissAction = true,
                duration = SnackbarDuration.Long
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                context.startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
            }
            viewModel.downloadResultShown()
        }
    }

    ManageAccountScreenContent(
        dateString = viewModel.dateString,
        inProgress = inProgress,
        padding = it,
        onDownloadData = viewModel::downloadAccountData,
        onDeleteAccountPressed = onDeleteAccountPressed,
    )
}

@Composable
private fun ManageAccountScreenContent(
    dateString: String,
    inProgress: Boolean,
    padding: PaddingValues = PaddingValues(),
    onDownloadData: () -> Unit = {},
    onDeleteAccountPressed: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .navigationBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween
) {
    Column(
        horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.MyAccount_Title_COPY),
            style = MaterialTheme.typography.titleLarge.copy(
                color = TextGreen, textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "${stringResource(R.string.MyAccount_AccountCreatedOn_COPY)} $dateString",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = ColorGrayScaleBlack, textAlign = TextAlign.Start
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
                    text = stringResource(R.string.MyAccount_PersonalDataDisclaimer_COPY),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        PrimaryButton(
            text = stringResource(R.string.MyAccount_DownloadDataButton_COPY),
            enabled = !inProgress,
            onClick = onDownloadData,
            modifier = Modifier.fillMaxWidth()
        )
    }
    OutlinedButton(
        shape = RoundedCornerShape(CornerSize(6.dp)),
        onClick = onDeleteAccountPressed,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorAlertRed),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.MyAccount_DeleteAccountButton_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = ColorAlertRed, fontWeight = FontWeight.SemiBold
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