package nl.eduid.screens.deleteaccountsecondconfirm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import nl.eduid.ui.keyboardAsState
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
    var waitingForVmEvent by rememberSaveable { mutableStateOf(false) }
    val owner = LocalLifecycleOwner.current

    if (waitingForVmEvent && viewModel.uiState.isDeleted != null) {
        val currentOnAccountDeleted by rememberUpdatedState(onAccountDeleted)
        LaunchedEffect(owner) {
            currentOnAccountDeleted()
            waitingForVmEvent = false
        }
    }

    DeleteAccountSecondConfirmScreenContent(
        fullNameInput = viewModel.uiState.fullName,
        padding = it,
        onInputChange = { viewModel.onInputChange(it) },
        errorData = viewModel.uiState.errorData,
        dismissError = viewModel::clearErrorData,
        inProgress = viewModel.uiState.inProgress,
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
    padding: PaddingValues = PaddingValues(),
    onInputChange: (String) -> Unit = {},
    inProgress: Boolean = false,
    errorData: ErrorData? = null,
    dismissError: () -> Unit = {},
    onDeleteAccountPressed: () -> Unit = {},
    goBack: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween
) {
    if (errorData != null) {
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = dismissError
        )
    }
    Column(
        horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val isKeyboardOpen by keyboardAsState()
        AnimatedVisibility(
            !isKeyboardOpen, Modifier.fillMaxWidth()
        ) {
            Column() {
                Text(
                    text = stringResource(R.string.Account_DeleteAccountSure_COPY),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextGreen, textAlign = TextAlign.Start
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(18.dp))
            }
        }
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
                text = stringResource(R.string.ConfirmDelete_Disclaimer_COPY),
                modifier = Modifier.constrainAs(text) {
                    start.linkTo(image.end)
                    end.linkTo(parent.end, margin = 12.dp)
                    top.linkTo(parent.top, margin = 12.dp)
                    bottom.linkTo(parent.bottom, margin = 12.dp)
                    width = Dimension.fillToConstraints
                })
        }
        AnimatedVisibility(
            !isKeyboardOpen, Modifier.fillMaxWidth()
        ) {
            Column() {
                Spacer(Modifier.height(18.dp))
                Text(
                    text = stringResource(R.string.ConfirmDelete_TypeNameToConfirm_COPY),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextBlack, textAlign = TextAlign.Start
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(24.dp))
            }
        }
        OutlinedTextField(
            value = fullNameInput,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            onValueChange = { onInputChange(it) },
            label = { Text(stringResource(R.string.ConfirmDelete_YourFullNameLabel_COPY)) },
            placeholder = { Text(stringResource(R.string.ConfirmDelete_Placeholder_COPY)) },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(bottom = 24.dp),
    ) {
        PrimaryButton(
            modifier = Modifier.widthIn(min = 140.dp),
            text = stringResource(R.string.Button_Cancel_COPY),
            onClick = goBack,
            buttonBackgroundColor = Color.Transparent,
            buttonTextColor = TextGrey,
            buttonBorderColor = ButtonBorderGrey,
        )
        PrimaryButton(
            modifier = Modifier.widthIn(min = 140.dp),
            text = stringResource(R.string.ConfirmDelete_Button_Confirm_COPY),
            onClick = onDeleteAccountPressed,
            buttonBackgroundColor = ButtonRed,
            buttonTextColor = Color.White,
            enabled = fullNameInput.isNotBlank(),
        )
    }
}


@Preview()
@Composable
private fun DeleteAccountSecondConfirmScreen() {
    EduidAppAndroidTheme {
        DeleteAccountSecondConfirmScreenContent()
    }
}