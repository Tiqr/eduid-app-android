package nl.eduid.screens.requestiddetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.CheckToSAndPrivacyPolicy
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.ColorScale_Gray_Black
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.outlinedTextColors

@Composable
fun RequestEduIdFormScreen(
    goToNextScreen: (emailAddress: String, codeHash: String) -> Unit,
    onBackClicked: () -> Unit,
    viewModel: RequestEduIdFormViewModel,
) = EduIdTopAppBar(
    onBackClicked = onBackClicked
) {
    var processingRequest by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    viewModel.inputForm.errorData?.let { errorData ->
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = viewModel::dismissError
        )
    }
    if (processingRequest && viewModel.inputForm.requestComplete) {
        val currentRequest by rememberUpdatedState(goToNextScreen)
        LaunchedEffect(viewModel) {
            processingRequest = false
            currentRequest(viewModel.inputForm.email, viewModel.resendOneTimeCodeHash.value!!)
        }
    }
    RequestEduIdFormContent(inputFormData = viewModel.inputForm,
        padding = it,
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
@OptIn(
    ExperimentalLayoutApi::class
)
private fun RequestEduIdFormContent(
    inputFormData: InputForm,
    padding: PaddingValues = PaddingValues(),
    onEmailChange: (String) -> Unit = {},
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onAcceptToC: (Boolean) -> Unit = {},
    onRequestEduIdAccount: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(padding)
        .padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val isKeyboardOpen by rememberUpdatedState(WindowInsets.isImeVisible)
        AnimatedVisibility(
            !isKeyboardOpen,
            Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = stringResource(R.string.CreateEduID_Create_MainTitleLabel_COPY),
                    style = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }
        }
        if (inputFormData.isProcessing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
        Text(
            stringResource(R.string.CreateEduID_EnterPersonalInfo_EmailFieldTitle_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        OutlinedTextField(
            colors = outlinedTextColors(),
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
            placeholder = { Text(stringResource(R.string.CreateEduID_EnterPersonalInfo_EmailFieldPlaceHolder_COPY)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            stringResource(R.string.Login_GivenName_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        OutlinedTextField(
            colors = outlinedTextColors(),
            value = inputFormData.firstName,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }),
            onValueChange = onFirstNameChange,
            placeholder = { Text(stringResource(R.string.Login_GivenNamePlaceholder_COPY)) },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            stringResource(R.string.Login_FamilyName_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        OutlinedTextField(
            colors = outlinedTextColors(),
            value = inputFormData.lastName,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            onValueChange = onLastNameChange,
            placeholder = { Text(stringResource(R.string.Login_FamilyNamePlaceholder_COPY)) },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        CheckToSAndPrivacyPolicy(
            onAcceptChange = onAcceptToC,
            hasAcceptedToC = inputFormData.termsAccepted,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        LaunchedEffect(focusRequester) {
            awaitFrame()
            focusRequester.requestFocus()
        }
    }
    PrimaryButton(
        text = stringResource(R.string.CreateEduID_Explanation_CreateEduidButton_COPY),
        onClick = onRequestEduIdAccount,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
            .padding(bottom = 24.dp),
        enabled = inputFormData.isFormValid,
    )
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



