package nl.eduid.screens.resetpasswordconfirm

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.ButtonBorderGrey
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack
import nl.eduid.ui.theme.TextGrey

@Composable
fun ResetPasswordConfirmScreen(
    viewModel: ResetPasswordConfirmViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack,
    snackbarHostState = snackbarHostState,
) {
    val uiState by viewModel.uiState.observeAsState(UiState())
    uiState.isCompleted?.let {
        val snackbarText = stringResource(R.string.reset_password_completed)
        LaunchedEffect(snackbarHostState, viewModel, snackbarText) {
            snackbarHostState.showSnackbar(snackbarText)
            viewModel.completedShown()
        }
    }

    ResetPasswordConfirmScreenContent(
        newPasswordInput = uiState.newPasswordInput,
        confirmPasswordInput = uiState.confirmPasswordInput,
        inProgress = uiState.inProgress,
        errorData = uiState.errorData,
        dismissError = viewModel::clearErrorState,
        onNewPasswordChange = { viewModel.onNewPasswordInput(it) },
        onConfirmPasswordChange = { viewModel.onConfirmPasswordInput(it) },
        onResetPasswordClicked = { viewModel.onResetPasswordClicked() },
    ) { viewModel.onDeletePasswordClicked() }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ResetPasswordConfirmScreenContent(
    newPasswordInput: String = "",
    confirmPasswordInput: String = "",
    inProgress: Boolean = false,
    errorData: ErrorData? = null,
    dismissError:()->Unit = {},
    onNewPasswordChange: (newValue: String) -> Unit = {},
    onConfirmPasswordChange: (newValue: String) -> Unit = {},
    onResetPasswordClicked: () -> Unit = {},
    onDeletePasswordClicked: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    if (errorData != null) {
        AlertDialogWithSingleButton(
            title = errorData.title,
            explanation = errorData.message,
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = dismissError
        )
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (body, bottomColumn) = createRefs()
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .constrainAs(body) {
                    linkTo(parent.top, bottomColumn.top, bias = 0F)
                }
                .verticalScroll(rememberScrollState())

        ) {
            Spacer(Modifier.height(36.dp))
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start,
                    color = ButtonGreen
                ),
                text = stringResource(R.string.reset_password_confirm_title),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))

            if (inProgress) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
            }
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                text = stringResource(R.string.reset_password_confirm_subtitle),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = newPasswordInput,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                onValueChange = { onNewPasswordChange(it) },
                label = { Text(stringResource(R.string.reset_password_password_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = confirmPasswordInput,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                onValueChange = { onConfirmPasswordChange(it) },
                label = { Text(stringResource(R.string.reset_password_repeat_password_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            PrimaryButton(
                text = stringResource(R.string.button_reset_password),
                enabled = !inProgress,
                onClick = onResetPasswordClicked,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(30.dp))
            Divider(color = TextBlack, thickness = 1.dp)
            Spacer(Modifier.height(16.dp))
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start,
                    color = ButtonGreen
                ),
                text = stringResource(R.string.reset_password_confirm_second_title),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                text = stringResource(R.string.reset_password_confirm_subtitle),
                modifier = Modifier
                    .fillMaxWidth()
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
    ResetPasswordConfirmScreenContent(
        onResetPasswordClicked = { },
    )
}