package nl.eduid.screens.recovery.confirmsms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import nl.eduid.R
import nl.eduid.screens.recovery.UiState
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun ConfirmCodeScreen(
    viewModel: ConfirmCodeViewModel,
    phoneNumber: String,
    goToStartScreen: () -> Unit,
    goBack: () -> Unit,
) =  EduIdTopAppBar(
    onBackClicked = goBack
) {
    var waitForVmEvent by rememberSaveable { mutableStateOf(false) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    if (waitForVmEvent) {
        val currentGoToStartScreen by rememberUpdatedState(newValue = goToStartScreen)
        LaunchedEffect(viewModel, lifecycle) {
            snapshotFlow { viewModel.uiState }.distinctUntilChanged()
                .filter { it.isCompleted != null }.flowWithLifecycle(lifecycle).collect {
                    waitForVmEvent = false
                    currentGoToStartScreen()
                    viewModel.clearCompleted()
                }
        }
    }

    ConfirmCodeContent(
        uiState = viewModel.uiState,
        phoneNumber = phoneNumber,
        padding = it,
        onClick = {
            waitForVmEvent = true
            viewModel.confirmPhoneCode()
        },
        dismissError = viewModel::dismissError,
        onValueChange = { viewModel.onCodeChange(it) },
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmCodeContent(
    uiState: UiState,
    phoneNumber: String,
    padding: PaddingValues = PaddingValues(),
    onClick: () -> Unit = {},
    dismissError: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

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
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.PinAndBioMetrics_CheckMessages_COPY),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.PinAndBioMetrics_EnterSixDigitCode_COPY, phoneNumber),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        val clipboardManager = LocalClipboardManager.current
        OutlinedTextField(
            value = uiState.input,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && clipboardManager.hasText()) {
                        val clipText = clipboardManager.getText()?.text ?: ""
                        if (clipText.isNotEmpty() && clipText.length == 6) {
                            onValueChange(clipText)
                        }
                    }
                }
                .requiredHeight(TextFieldDefaults.MinHeight)
        )
//        We can either have automatic focus on text field and showing keyboard
//        OR
//        We have automatic paste for the SMS code when the focus is done manually.
//        LaunchedEffect(focusRequester) {
//            awaitFrame()
//            focusRequester.requestFocus()
//        }
    }
    PrimaryButton(
        text = stringResource(R.string.PhoneVerification_Verify_COPY),
        enabled = uiState.input.isNotEmpty(),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
            .padding(bottom = 24.dp),
    )
}

@Preview()
@Composable
private fun Preview_ConfirmCodeContent() {
    EduidAppAndroidTheme {
        ConfirmCodeContent(UiState("12345"), "065555555")
    }
}
