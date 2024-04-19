package nl.eduid.screens.resetpassword

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import nl.eduid.R
import nl.eduid.graphs.RequestEduIdLinkSent.ADD_PASSWORD_REASON
import nl.eduid.graphs.RequestEduIdLinkSent.CHANGE_PASSWORD_REASON
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.ButtonBorderGrey
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.MainSurfGreen
import nl.eduid.ui.theme.TextGrey

@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel,
    goToEmailSent: (String, String) -> Unit = { _, _ -> },
    goBack: () -> Unit = {},
) = EduIdTopAppBar(
    onBackClicked = goBack,
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
        val currentGoToEmailSent by rememberUpdatedState(newValue = goToEmailSent)
        LaunchedEffect(viewModel, lifecycle) {
            snapshotFlow { viewModel.uiState }.distinctUntilChanged()
                .filter { it.isCompleted != null }.flowWithLifecycle(lifecycle).collect {
                    waitForVmEvent = false
                    currentGoToEmailSent(
                        viewModel.uiState.emailUsed,
                        if (viewModel.uiState.password == Password.Add) ADD_PASSWORD_REASON else CHANGE_PASSWORD_REASON
                    )
                    viewModel.clearCompleted()
                }
        }
    }

    ResetPasswordScreenContent(
        password = viewModel.uiState.password,
        inProgress = viewModel.uiState.inProgress,
        padding = it,
        onResetPasswordClicked = {
            viewModel.resetPasswordLink()
            waitForVmEvent = true
        },
        goBack = goBack,
    )
}

@Composable
fun ResetPasswordScreenContent(
    password: Password = Password.Add,
    inProgress: Boolean,
    padding: PaddingValues = PaddingValues(),
    onResetPasswordClicked: () -> Unit = {},
    goBack: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .navigationBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        if (inProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start, color = MainSurfGreen
                ), text = if (password == Password.Add) {
                    stringResource(R.string.PasswordResetLink_Title_AddPassword_COPY)
                } else {
                    stringResource(R.string.PasswordResetLink_Title_ChangePassword_COPY)
                }, modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
            text = if (password == Password.Add) {
                stringResource(R.string.PasswordResetLink_Description_AddPassword_COPY)
            } else {
                stringResource(R.string.PasswordResetLink_Description_ChangePassword_COPY)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(36.dp))
    }
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        PrimaryButton(
            enabled = !inProgress,
            modifier = Modifier.widthIn(min = 140.dp),
            text = stringResource(R.string.PasswordResetLink_Button_Cancel_COPY),
            onClick = goBack,
            buttonBackgroundColor = Color.Transparent,
            buttonTextColor = TextGrey,
            buttonBorderColor = ButtonBorderGrey,
        )
        PrimaryButton(
            enabled = !inProgress,
            modifier = Modifier.widthIn(min = 140.dp),
            text = stringResource(R.string.PasswordResetLink_Button_SendEmail_COPY),
            onClick = onResetPasswordClicked,
            buttonTextColor = Color.White,
        )
    }

}

@Preview
@Composable
private fun PreviewResetPasswordScreenContent() = EduidAppAndroidTheme {
    ResetPasswordScreenContent(
        password = Password.Add, inProgress = false
    )
}