package nl.eduid.screens.editemail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.theme.ButtonBlue
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun EditEmailScreen(
    viewModel: EditEmailViewModel,
    onSaveNewEmailRequested: (newEmail: String) -> Unit,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack
) {
    var waitForVmEvent by rememberSaveable { mutableStateOf(false) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    if (waitForVmEvent) {
        val currentGoToEmailSent by rememberUpdatedState(newValue = onSaveNewEmailRequested)
        LaunchedEffect(viewModel, lifecycle) {
            snapshotFlow { viewModel.uiState }.distinctUntilChanged()
                .filter { it.isCompleted != null }.flowWithLifecycle(lifecycle).collect {
                    waitForVmEvent = false
                    currentGoToEmailSent(viewModel.uiState.email)
                }
        }
    }
    viewModel.uiState.errorData?.let { errorData ->
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = viewModel::dismissError
        )
    }

    EditEmailScreenContent(
        uiState = viewModel.uiState,
        padding = it,
        onNewEmailRequestClicked = {
            waitForVmEvent = true
            viewModel.requestEmailChangeClicked(it)
        },
        onEmailTextChange = viewModel::onEmailChange,
        goBack = goBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EditEmailScreenContent(
    uiState: UiState,
    padding: PaddingValues = PaddingValues(),
    onEmailTextChange: (String) -> Unit = {},
    onNewEmailRequestClicked: (newEmail: String) -> Unit = {},
    goBack: () -> Unit,
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }
        Text(
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start, color = ButtonGreen
            ), text = stringResource(R.string.edit_email_title), modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        if (uiState.inProgress) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }

        Text(
            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
            text = stringResource(R.string.edit_email_subtitle),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.email,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            isError = !uiState.isEmailValid,
            onValueChange = { onEmailTextChange(it) },
            label = { Text(stringResource(R.string.edit_email_new_email_title)) },
            placeholder = { Text(stringResource(R.string.request_id_details_screen_email_input_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)

        )
        LaunchedEffect(focusRequester) {
            awaitFrame()
            focusRequester.requestFocus()
        }
    }
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
            .padding(bottom = 24.dp),
    ) {
        SecondaryButton(
            modifier = Modifier.widthIn(min = 140.dp),
            text = stringResource(R.string.edit_email_cancel_button),
            onClick = goBack,
        )
        PrimaryButton(
            modifier = Modifier.widthIn(min = 140.dp),
            text = stringResource(R.string.edit_email_confirm_button),
            onClick = { onNewEmailRequestClicked(uiState.email) },
            buttonBackgroundColor = ButtonBlue,
            buttonTextColor = Color.White,
            enabled = uiState.isEmailValid,
        )
    }
}


@Preview
@Composable
private fun PreviewEditEmailScreenContent() = EduidAppAndroidTheme {
    EditEmailScreenContent(
        uiState = UiState(),
        onEmailTextChange = {},
        goBack = { },
    )
}