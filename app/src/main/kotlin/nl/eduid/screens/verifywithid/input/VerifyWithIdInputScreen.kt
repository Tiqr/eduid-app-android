package nl.eduid.screens.verifywithid.input

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.model.ControlCode
import nl.eduid.di.model.ControlCodeRequest
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.outlinedTextColors


@Composable
fun VerifyWithIdInputScreen(
    viewModel: VerifyWithIdInputViewModel,
    goBack: () -> Unit,
    goToGeneratedCode: (ControlCode) -> Unit
) = EduIdTopAppBar(
    onBackClicked = goBack
) { padding ->

    LaunchedEffect(viewModel.uiState.createdControlCode) {
        viewModel.uiState.createdControlCode?.let {
            goToGeneratedCode(it)
        }
    }

    VerifyWithIdInputScreenContent(
        padding = padding,
        editCode = viewModel.uiState.editCode,
        isLoading = viewModel.uiState.isLoading,
        errorData = viewModel.uiState.errorData,
        generateCode = viewModel::generateCode,
        dismissError = viewModel::dismissError
    )
}

@Composable
fun VerifyWithIdInputScreenContent(
    padding: PaddingValues = PaddingValues(),
    editCode: ControlCode?,
    isLoading: Boolean,
    errorData: ErrorData?,
    generateCode: (ControlCodeRequest) -> Unit,
    dismissError: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var firstName by remember { mutableStateOf(editCode?.firstName ?: "") }
    var lastName by remember { mutableStateOf(editCode?.lastName ?: "") }
    var dateOfBirth by remember { mutableStateOf(editCode?.dayOfBirth ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .systemBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdIntro_Title_FirstLine_COPY),
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdIntro_Title_SecondLine_COPY),
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)

        )
        Spacer(Modifier.height(24.dp))

        Image(
            modifier = Modifier
                .fillMaxWidth(),
            contentDescription = "ID card example",
            painter = painterResource(R.drawable.ic_verify_with_id_example)
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.ServiceDesk_IdCard_Information_COPY),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Last name
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(vertical = 8.dp),
            text = stringResource(R.string.ServiceDesk_IdCard_LastName_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        )
        OutlinedTextField(
            colors = outlinedTextColors(),
            value = lastName,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next, keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }),
            onValueChange = {
                lastName = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        // First name(s)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(vertical = 8.dp),
            text = stringResource(R.string.ServiceDesk_IdCard_FirstName_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        )
        OutlinedTextField(
            colors = outlinedTextColors(),
            value = firstName,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next, keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }),
            onValueChange = {
                firstName = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
        // Date of birth
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(vertical = 8.dp),
            text = stringResource(R.string.ServiceDesk_IdCard_DayOfBirth_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        )
        OutlinedTextField(
            colors = outlinedTextColors(),
            value = dateOfBirth,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            onValueChange = {
                dateOfBirth = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
        Spacer(Modifier.height(36.dp))
        PrimaryButton(
            enabled = !isLoading && firstName.isNotEmpty() && lastName.isNotEmpty() && dateOfBirth.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.ServiceDesk_IdCard_GenerateControlCode_COPY),
            onClick = {
                val payload = ControlCodeRequest(
                    firstName = firstName,
                    lastName = lastName,
                    dayOfBirth = dateOfBirth
                )
                generateCode(payload)
            }
        )
    }

    errorData?.let {
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = dismissError
        )
    }
}

@Composable
@Preview
fun VerifyWithIdInputScreenContent_Preview() {
    EduidAppAndroidTheme {
        VerifyWithIdInputScreenContent(
            isLoading = false,
            editCode = ControlCode(firstName = "First name", lastName = "Last name", dayOfBirth = "01-01-2000", code = "123456", createdAt = System.currentTimeMillis()),
            generateCode = { _ -> },
            errorData = null,
            dismissError = {}
        )
    }
}