package nl.eduid.screens.deleteaccountsecondconfirm

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.AlertRedBackground
import nl.eduid.ui.theme.ButtonBorderGrey
import nl.eduid.ui.theme.ButtonRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack
import nl.eduid.ui.theme.TextGreen
import nl.eduid.ui.theme.TextGrey

@Composable
fun DeleteAccountSecondConfirmScreen(
    viewModel: DeleteAccountSecondConfirmViewModel,
    onAccountDeleted: () -> Unit,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack,
) {
    val uiState by viewModel.uiState.observeAsState(UiState())
    var waitingForVmEvent by rememberSaveable { mutableStateOf(false) }
    val owner = LocalLifecycleOwner.current

    if (waitingForVmEvent && uiState.isDeleted != null) {
        val currentOnAccountDeleted by rememberUpdatedState(onAccountDeleted)
        LaunchedEffect(owner) {
            currentOnAccountDeleted()
            waitingForVmEvent = false
        }
    }

    DeleteAccountSecondConfirmScreenContent(
        fullNameInput = uiState.fullName,
        onInputChange = { viewModel.onInputChange(it) },
        errorData = uiState.errorData,
        dismissError = viewModel::clearErrorData,
        inProgress = uiState.inProgress,
        onDeleteAccountPressed = {
            viewModel.onDeleteAccountPressed()
            waitingForVmEvent = true
        },
        goBack = goBack,
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DeleteAccountSecondConfirmScreenContent(
    fullNameInput: String = "",
    onInputChange: (String) -> Unit = {},
    inProgress: Boolean = false,
    errorData: ErrorData? = null,
    dismissError: () -> Unit = {},
    onDeleteAccountPressed: () -> Unit = {},
    goBack: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
) {
    if (errorData != null) {
        AlertDialogWithSingleButton(
            title = errorData.title,
            explanation = errorData.message,
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = dismissError
        )
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    Spacer(Modifier.height(36.dp))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
    ) {
        Text(
            text = stringResource(R.string.delete_account_two_title),
            style = MaterialTheme.typography.titleLarge.copy(
                color = TextGreen, textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(18.dp))
        if (inProgress) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(18.dp))
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = AlertRedBackground)
        ) {
            val (image, text) = createRefs()
            Image(painter = painterResource(R.drawable.warning_icon_red),
                contentDescription = "",
                modifier = Modifier.constrainAs(image) {
                    top.linkTo(parent.top, margin = 12.dp)
                    start.linkTo(parent.start, margin = 12.dp)
                    end.linkTo(text.start, margin = 12.dp)
                })
            Text(style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                text = stringResource(R.string.delete_no_undo_warning),
                modifier = Modifier.constrainAs(text) {
                    start.linkTo(image.end)
                    end.linkTo(parent.end, margin = 12.dp)
                    top.linkTo(parent.top, margin = 12.dp)
                    bottom.linkTo(parent.bottom, margin = 12.dp)
                    width = Dimension.fillToConstraints
                })
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.delete_account_two_description),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextBlack, textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(36.dp))
        OutlinedTextField(
            value = fullNameInput,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            onValueChange = { onInputChange(it) },
            label = { Text(stringResource(R.string.managa_account_your_full_name)) },
            placeholder = { Text(stringResource(R.string.manage_account_full_name_explain)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
    Column(
        Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
        ) {
            PrimaryButton(
                modifier = Modifier.widthIn(min = 140.dp),
                text = stringResource(R.string.button_cancel),
                onClick = goBack,
                buttonBackgroundColor = Color.Transparent,
                buttonTextColor = TextGrey,
                buttonBorderColor = ButtonBorderGrey,
            )
            PrimaryButton(
                modifier = Modifier.widthIn(min = 140.dp),
                text = stringResource(R.string.button_confirm),
                onClick = onDeleteAccountPressed,
                buttonBackgroundColor = ButtonRed,
                buttonTextColor = Color.White,
                enabled = fullNameInput.isNotBlank(),
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}


@Preview()
@Composable
private fun DeleteAccountSecondConfirmScreen() {
    EduidAppAndroidTheme {
        DeleteAccountSecondConfirmScreenContent()
    }
}