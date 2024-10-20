package nl.eduid.screens.twofactorkeydelete

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.theme.ColorAlertRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.ColorScale_Gray_Black
import nl.eduid.ui.theme.ColorMain_Green_400
import nl.eduid.ui.theme.ColorSupport_Blue_100

@Composable
fun TwoFactorKeyDeleteScreen(
    viewModel: TwoFactorKeyDeleteViewModel,
    twoFaKeyId: String,
    goBack: () -> Unit,
    onDeleteDone: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack
) {
    var waitForVmEvent by rememberSaveable { mutableStateOf(false) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    viewModel.uiState.errorData?.let { errorData ->
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = viewModel::dismissError
        )
    }

    if (waitForVmEvent) {
        val currentOnDeleteDone by rememberUpdatedState(newValue = onDeleteDone)
        LaunchedEffect(viewModel, lifecycle) {
            snapshotFlow { viewModel.uiState }.distinctUntilChanged()
                .filter { it.isCompleted != null }.flowWithLifecycle(lifecycle).collect {
                    waitForVmEvent = false
                    currentOnDeleteDone()
                    viewModel.clearCompleted()
                }
        }
    }
    TwoFactorKeyDeleteScreenContent(
        inProgress = viewModel.uiState.inProgress,
        padding = it,
        onDeleteClicked = {
            waitForVmEvent = true
            viewModel.deleteKey(twoFaKeyId)
        },
        keyId = twoFaKeyId,
        goBack = goBack,
    )
}

@Composable
private fun TwoFactorKeyDeleteScreenContent(
    inProgress: Boolean = false,
    keyId: String,
    padding: PaddingValues = PaddingValues(),
    onDeleteClicked: () -> Unit = {},
    goBack: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .navigationBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text(
            text = stringResource(R.string.TwoFactorKeys_DeleteKey_COPY),
            style = MaterialTheme.typography.titleLarge.copy(
                color = ColorMain_Green_400, textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(18.dp))
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = ColorSupport_Blue_100)
        ) {
            val (image, text) = createRefs()
            Image(painter = painterResource(R.drawable.warning_icon_red),
                contentDescription = "",
                modifier = Modifier.constrainAs(image) {
                    top.linkTo(parent.top, margin = 12.dp)
                    start.linkTo(parent.start, margin = 12.dp)
                    end.linkTo(text.start, margin = 12.dp)
                })
            Text(style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                text = stringResource(R.string.ConfirmDelete_Disclaimer_COPY),
                modifier = Modifier.constrainAs(text) {
                    start.linkTo(image.end)
                    end.linkTo(parent.end, margin = 12.dp)
                    top.linkTo(parent.top, margin = 12.dp)
                    bottom.linkTo(parent.bottom, margin = 12.dp)
                    width = Dimension.fillToConstraints
                })
        }
        Spacer(Modifier.height(18.dp))
        if (inProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(18.dp))
        }
        Text(
            text = stringResource(R.string.Credential_DeleteCredentialConfirmation_COPY, keyId),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = ColorScale_Gray_Black, textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        SecondaryButton(
            enabled = !inProgress,
            text = stringResource(id = R.string.Button_Cancel_COPY),
            modifier = Modifier.widthIn(min = 140.dp),
            onClick = goBack,
        )
        PrimaryButton(
            enabled = !inProgress,
            text = stringResource(id = R.string.ConfirmDelete_Button_Confirm_COPY),
            modifier = Modifier.widthIn(min = 140.dp),
            onClick = onDeleteClicked,
            buttonBackgroundColor = ColorAlertRed,
        )
    }
}

@Preview()
@Composable
private fun PreviewTwoFactorKeyDeleteScreenContent() {
    EduidAppAndroidTheme {
        TwoFactorKeyDeleteScreenContent(keyId = "123", onDeleteClicked = { })
    }
}