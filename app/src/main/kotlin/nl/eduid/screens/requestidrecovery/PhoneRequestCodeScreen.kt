package nl.eduid.screens.requestidrecovery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun PhoneRequestCodeScreen(
    viewModel: PhoneRequestCodeViewModel,
    onBackClicked: () -> Unit,
    goToConfirmPhoneNumber: (phoneNumber: String) -> Unit,
) = EduIdTopAppBar(
    onBackClicked = onBackClicked
) {
    val uiState by viewModel.uiState.observeAsState(UiState())
    var canContinue by rememberSaveable { mutableStateOf(false) }

    if (canContinue && !uiState.inProgress && uiState.errorData == null) {
        val currentGoToConfirmNumber by rememberUpdatedState(newValue = goToConfirmPhoneNumber)
        LaunchedEffect(key1 = viewModel) {
            canContinue = false
            currentGoToConfirmNumber(uiState.input)
        }
    }

    PhoneRequestCodeContent(
        uiState = uiState,
        onClick = {
            canContinue = true
            viewModel.requestPhoneCode()
        },
        dismissError = viewModel::dismissError,
        onValueChange = { viewModel.onPhoneNumberChange(it) },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
private fun PhoneRequestCodeContent(
    uiState: UiState,
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

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (content, bottomButton, bottomSpacer) = createRefs()

        Column(horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(content) {
                    top.linkTo(parent.top)
                }) {

            Text(
                text = stringResource(R.string.request_id_recovery_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Text(
                text = stringResource(R.string.request_id_recovery_text_code),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )
            Text(
                text = stringResource(R.string.request_id_recovery_input_hint),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.input,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
        PrimaryButton(
            text = stringResource(R.string.request_id_recovery_button),
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomButton) {
                    bottom.linkTo(bottomSpacer.top)
                },
        )
        Spacer(
            Modifier
                .height(40.dp)
                .constrainAs(bottomSpacer) {
                    bottom.linkTo(parent.bottom)
                },
        )
    }
}

@Preview()
@Composable
private fun PreviewEnroll() {
    EduidAppAndroidTheme {
        PhoneRequestCodeContent(UiState("065555555"))
    }
}




