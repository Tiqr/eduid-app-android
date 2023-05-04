package nl.eduid.screens.requestidrecovery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun PhoneRequestCodeScreen(
    viewModel: PhoneRequestCodeViewModel,
    onBackClicked: () -> Unit,
    goToConfirmPhoneNumber: (phoneNumber: String) -> Unit,
) = EduIdTopAppBar(
    onBackClicked = onBackClicked
) {
    var waitForVmEvent by rememberSaveable { mutableStateOf(false) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    if (waitForVmEvent) {
        val currentGoToConfirmNumber by rememberUpdatedState(newValue = goToConfirmPhoneNumber)
        LaunchedEffect(viewModel, lifecycle) {
            snapshotFlow { viewModel.uiState }.distinctUntilChanged()
                .filter { it.isCompleted != null }.flowWithLifecycle(lifecycle).collect {
                    waitForVmEvent = false
                    currentGoToConfirmNumber(it.input)
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

    PhoneRequestCodeContent(
        uiState = viewModel.uiState,
        onClick = {
            waitForVmEvent = true
            viewModel.requestPhoneCode()
        },
        onValueChange = { viewModel.onPhoneNumberChange(it) },
    )
}

@Composable
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
)
private fun PhoneRequestCodeContent(
    uiState: UiState,
    onClick: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
) {
    Column(
        horizontalAlignment = Alignment.Start, modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
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
            modifier = Modifier.height(16.dp)
        )
        Text(
            text = stringResource(R.string.request_id_recovery_input_hint),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(
            value = uiState.input,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(TextFieldDefaults.MinHeight)
        )
    }
    PrimaryButton(
        text = stringResource(R.string.request_id_recovery_button),
        onClick = onClick,
        enabled = uiState.input.isNotEmpty(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 24.dp),
    )
}

@Preview()
@Composable
private fun PreviewEnroll() {
    EduidAppAndroidTheme {
        PhoneRequestCodeContent(UiState("065555555"))
    }
}




