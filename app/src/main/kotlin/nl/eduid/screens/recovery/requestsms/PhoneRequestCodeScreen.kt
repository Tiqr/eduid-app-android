package nl.eduid.screens.recovery.requestsms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import nl.eduid.screens.recovery.UiState
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
) { paddingValues ->
    var waitForVmEvent by rememberSaveable { mutableStateOf(false) }

    if (waitForVmEvent) {
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        val currentGoToConfirmNumber by rememberUpdatedState(newValue = goToConfirmPhoneNumber)
        LaunchedEffect(viewModel, lifecycle) {
            snapshotFlow { viewModel.uiState }
                .distinctUntilChanged()
                .filter { state -> state.isCompleted != null }
                .flowWithLifecycle(lifecycle)
                .collect {
                    waitForVmEvent = false
                    currentGoToConfirmNumber(it.input)
                    viewModel.clearCompleted()
                }
        }
    }
    viewModel.uiState.errorData?.let { errorData ->
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = viewModel::dismissError
        )
    }

    PhoneRequestCodeContent(
        uiState = viewModel.uiState,
        padding = paddingValues,
        onClick = {
            viewModel.requestPhoneCode()
            waitForVmEvent = true
        },
        onValueChange = { viewModel.onPhoneNumberChange(it) },
    )
}

@Composable
@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
private fun PhoneRequestCodeContent(
    uiState: UiState,
    padding: PaddingValues = PaddingValues(),
    onClick: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
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
        val isKeyboardOpen by rememberUpdatedState(WindowInsets.isImeVisible)

        Text(
            text = stringResource(R.string.Recovery_PhoneNumber_COPY),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )

        AnimatedVisibility(
            !isKeyboardOpen, Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                Text(
                    text = stringResource(R.string.Recovery_Info_COPY),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }

        Text(
            text = stringResource(R.string.PhoneVerification_Text_COPY),
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
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(TextFieldDefaults.MinHeight)
        )
    }
    PrimaryButton(
        text = stringResource(R.string.Profile_Verify_COPY),
        onClick = onClick,
        enabled = uiState.input.isNotEmpty(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .navigationBarsPadding()
            .imePadding(),
    )
}

@Preview()
@Composable
private fun PreviewEnroll() {
    EduidAppAndroidTheme {
        PhoneRequestCodeContent(UiState("065555555"))
    }
}




