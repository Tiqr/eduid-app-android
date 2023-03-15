package nl.eduid.screens.requestidpin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.screens.requestidrecovery.UiState
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun ConfirmCodeScreen(
    viewModel: ConfirmCodeViewModel,
    phoneNumber: String,
    goToStartScreen: () -> Unit,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack
) {
    val uiState by viewModel.uiState.observeAsState(UiState())
    var canContinue by rememberSaveable { mutableStateOf(false) }

    if (canContinue && !uiState.inProgress && uiState.errorData == null) {
        val currentGoToStartScreen by rememberUpdatedState(newValue = goToStartScreen)
        LaunchedEffect(key1 = viewModel) {
            canContinue = false
            currentGoToStartScreen()
        }
    }

    ConfirmCodeContent(
        uiState = uiState,
        phoneNumber = phoneNumber,
        onClick = {
            canContinue = true
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
    onClick: () -> Unit = {},
    dismissError: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    if (uiState.errorData != null) {
        AlertDialogWithSingleButton(
            title = uiState.errorData.title,
            explanation = uiState.errorData.message,
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = dismissError
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.confirm_sms_code_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.confirm_sms_code_subtitle, phoneNumber),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.input,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(52.dp))
        }
        PrimaryButton(
            text = stringResource(R.string.confirm_sms_code_button),
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
        )
    }
}

@Preview()
@Composable
private fun Preview_ConfirmCodeContent() {
    EduidAppAndroidTheme {
        ConfirmCodeContent(UiState("12345"), "065555555")
    }
}
