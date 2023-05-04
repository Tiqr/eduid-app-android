package nl.eduid.screens.resetpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import nl.eduid.R
import nl.eduid.graphs.RequestEduIdLinkSent.ADD_PASSWORD_REASON
import nl.eduid.graphs.RequestEduIdLinkSent.CHANGE_PASSWORD_REASON
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.ButtonBlue
import nl.eduid.ui.theme.ButtonBorderGrey
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme
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
            buttonLabel = stringResource(R.string.button_ok),
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
    onResetPasswordClicked: () -> Unit = {},
    goBack: () -> Unit = {},
) = ConstraintLayout(
    modifier = Modifier.fillMaxSize()
) {

    val (body, bottomColumn) = createRefs()
    Column(verticalArrangement = Arrangement.Top, modifier = Modifier
        .constrainAs(body) {
            linkTo(parent.top, bottomColumn.top, bias = 0F)
        }
        .verticalScroll(rememberScrollState())

    ) {
        Spacer(Modifier.height(36.dp))
        if (inProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start, color = ButtonGreen
                ), text = if (password == Password.Add) {
                    stringResource(R.string.add_password_title)
                } else {
                    stringResource(R.string.change_password_title)
                }, modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
            text = if (password == Password.Add) {
                stringResource(R.string.add_password_subtitle)
            } else {
                stringResource(R.string.change_password_subtitle)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(36.dp))
    }
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.constrainAs(bottomColumn) {
            bottom.linkTo(parent.bottom, margin = 24.dp)
        },
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
            ) {
                PrimaryButton(
                    enabled = !inProgress,
                    modifier = Modifier.widthIn(min = 140.dp),
                    text = stringResource(R.string.reset_password_cancel_button),
                    onClick = goBack,
                    buttonBackgroundColor = Color.Transparent,
                    buttonTextColor = TextGrey,
                    buttonBorderColor = ButtonBorderGrey,
                )
                PrimaryButton(
                    enabled = !inProgress,
                    modifier = Modifier.widthIn(min = 140.dp),
                    text = stringResource(R.string.reset_password_confirm_button),
                    onClick = onResetPasswordClicked,
                    buttonBackgroundColor = ButtonBlue,
                    buttonTextColor = Color.White,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewResetPasswordScreenContent() = EduidAppAndroidTheme {
    ResetPasswordScreenContent(
        password = Password.Add, inProgress = false
    )
}