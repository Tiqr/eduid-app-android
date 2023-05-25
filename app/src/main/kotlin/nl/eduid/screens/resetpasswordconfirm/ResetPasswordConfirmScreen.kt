package nl.eduid.screens.resetpasswordconfirm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.keyboardAsState
import nl.eduid.ui.theme.ButtonBorderGrey
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack
import nl.eduid.ui.theme.TextGrey

@Composable
fun ResetPasswordConfirmScreen(
    viewModel: ResetPasswordConfirmViewModel,
    isAddPassword: Boolean,
    goBack: () -> Unit,
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
        val currentGoBack by rememberUpdatedState(newValue = goBack)
        LaunchedEffect(viewModel, lifecycle) {
            snapshotFlow { viewModel.uiState }.distinctUntilChanged()
                .filter { it.isCompleted != null }.flowWithLifecycle(lifecycle).collect {
                    waitForVmEvent = false
                    currentGoBack()
                    viewModel.clearCompleted()
                }
        }
    }

    ResetPasswordConfirmScreenContent(
        newPasswordInput = viewModel.uiState.newPasswordInput,
        confirmPasswordInput = viewModel.uiState.confirmPasswordInput,
        isAddPassword = isAddPassword,
        inProgress = viewModel.uiState.inProgress,
        padding = it,
        onNewPasswordChange = { viewModel.onNewPasswordInput(it) },
        onConfirmPasswordChange = {
            viewModel.onConfirmPasswordInput(it)
        },
        onResetPasswordClicked = {
            viewModel.onResetPasswordClicked()
            waitForVmEvent = true
        },
    ) {
        viewModel.onDeletePasswordClicked()
        waitForVmEvent = true
    }
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun ResetPasswordConfirmScreenContent(
    newPasswordInput: String = "",
    confirmPasswordInput: String = "",
    isAddPassword: Boolean = false,
    inProgress: Boolean = false,
    padding: PaddingValues = PaddingValues(),
    onNewPasswordChange: (newValue: String) -> Unit = {},
    onConfirmPasswordChange: (newValue: String) -> Unit = {},
    onResetPasswordClicked: () -> Unit = {},
    onDeletePasswordClicked: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(padding)
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    ) {
        val isKeyboardOpen by keyboardAsState()
        AnimatedVisibility(
            !isKeyboardOpen,
        ) {
            Column() {
                Text(
                    style = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.Start, color = ButtonGreen
                    ), text = if (isAddPassword) {
                        stringResource(R.string.reset_password_add_title)
                    } else {
                        stringResource(R.string.reset_password_change_title)
                    }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(32.dp))
            }
        }

        if (inProgress) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }
        Text(
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign =
                TextAlign.Start
            ),
            text = stringResource(R.string.reset_password_confirm_subtitle),
            modifier = Modifier.fillMaxWidth()
        )
        val bringIntoViewRequester = BringIntoViewRequester()
        Column(
            modifier = Modifier
                .imePadding()
                .bringIntoViewRequester(bringIntoViewRequester)
        ) {
            val coroutineScope = rememberCoroutineScope()
            val focusManager = LocalFocusManager.current
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = newPasswordInput,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next, keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                onValueChange = { onNewPasswordChange(it) },
                label = { Text(stringResource(R.string.reset_password_password_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusEvent { event ->
                        if (event.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPasswordInput,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }),
                onValueChange = { onConfirmPasswordChange(it) },
                label = { Text(stringResource(R.string.reset_password_repeat_password_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusEvent { event ->
                        if (event.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }

            )
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                text = if (isAddPassword) {
                    stringResource(R.string.button_add_password)
                } else {
                    stringResource(R.string.button_reset_password)
                },
                enabled = !inProgress,
                onClick = onResetPasswordClicked,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        if (!isAddPassword) {
            Spacer(Modifier.height(16.dp))
            Divider(color = TextBlack, thickness = 1.dp)
            Spacer(Modifier.height(16.dp))
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start, color = ButtonGreen
                ),
                text = stringResource(R.string.reset_password_confirm_second_title),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                text = stringResource(R.string.reset_password_confirm_second_subtitle),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.button_delete_password),
                enabled = !inProgress,
                onClick = onDeletePasswordClicked,
                buttonBackgroundColor = Color.Transparent,
                buttonTextColor = TextGrey,
                buttonBorderColor = ButtonBorderGrey,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewResetPasswordConfirmScreenContent() = EduidAppAndroidTheme {
    ResetPasswordConfirmScreenContent()
}