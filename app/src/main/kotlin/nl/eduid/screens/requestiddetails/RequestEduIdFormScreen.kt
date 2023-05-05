package nl.eduid.screens.requestiddetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.CheckToSAndPrivacyPolicy
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun RequestEduIdFormScreen(
    goToEmailLinkSent: (emailAddress: String) -> Unit,
    onBackClicked: () -> Unit,
    viewModel: RequestEduIdFormViewModel,
) = EduIdTopAppBar(
    onBackClicked = onBackClicked
) {
    var processingRequest by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    if (viewModel.inputForm.errorData != null) {
        AlertDialogWithSingleButton(
            title = viewModel.inputForm.errorData!!.title,
            explanation = viewModel.inputForm.errorData!!.message,
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = viewModel::dismissError
        )
    }
    if (processingRequest && viewModel.inputForm.requestComplete) {
        val currentRequest by rememberUpdatedState(goToEmailLinkSent)
        LaunchedEffect(viewModel) {
            processingRequest = false
            currentRequest(viewModel.inputForm.email)
        }
    }
    RequestEduIdFormContent(inputFormData = viewModel.inputForm,
        onEmailChange = { viewModel.onEmailChange(it) },
        onFirstNameChange = { viewModel.onFirstNameChange(it) },
        onLastNameChange = { viewModel.onLastNameChange(it) },
        onAcceptToC = { viewModel.onTermsAccepted(it) },
        onRequestEduIdAccount = {
            processingRequest = true
            viewModel.requestNewEduIdAccount(context)
        })
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
private fun RequestEduIdFormContent(
    inputFormData: InputForm,
    onEmailChange: (String) -> Unit = {},
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onAcceptToC: (Boolean) -> Unit = {},
    onRequestEduIdAccount: () -> Unit = {},
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (content, bottomButton, bottomSpacer) = createRefs()
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        Column(horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(content) {
                    top.linkTo(parent.top)
                }) {
            Text(
                text = stringResource(R.string.request_id_details_screen_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )
            if (inputFormData.isProcessing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = inputFormData.email,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next, keyboardType = KeyboardType.Email
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(
                        FocusDirection.Down
                    )
                }),
                isError = (inputFormData.email.length > 2 && !inputFormData.emailValid),
                onValueChange = onEmailChange,
                label = { Text(stringResource(R.string.request_id_details_screen_email_input_title)) },
                placeholder = { Text(stringResource(R.string.request_id_details_screen_email_input_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = inputFormData.firstName,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(
                        FocusDirection.Down
                    )
                }),
                onValueChange = onFirstNameChange,
                label = { Text(stringResource(R.string.request_id_details_screen_first_name_input_title)) },
                placeholder = { Text(stringResource(R.string.request_id_details_screen_first_name_input_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = inputFormData.lastName,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                onValueChange = onLastNameChange,
                label = { Text(stringResource(R.string.request_id_details_screen_last_name_input_title)) },
                placeholder = { Text(stringResource(R.string.request_id_details_screen_last_name_input_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            CheckToSAndPrivacyPolicy(
                onAcceptChange = onAcceptToC,
                hasAcceptedToC = inputFormData.termsAccepted,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

        }
        PrimaryButton(
            text = stringResource(R.string.request_id_screen_create_id_button),
            onClick = onRequestEduIdAccount,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomButton) {
                    bottom.linkTo(bottomSpacer.top)
                },
            enabled = inputFormData.isFormValid,
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
        RequestEduIdFormContent(
            inputFormData = InputForm("rincewind@unseenuni.ank", "Rincewind", "Smith"),
        )
    }
}



