package nl.eduid.screens.editname

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.TwoColorTitle
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun EditNameFormScreen(
    viewModel: EditNameFormViewModel,
    onNameChangeDone: () -> Unit,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack
) {
    var waitForVmEvent by rememberSaveable { mutableStateOf(false) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    if (waitForVmEvent) {
        val currentGoToEmailSent by rememberUpdatedState(newValue = onNameChangeDone)
        LaunchedEffect(viewModel, lifecycle) {
            snapshotFlow { viewModel.uiState }.distinctUntilChanged()
                .filter { it.isCompleted != null }.flowWithLifecycle(lifecycle).collect {
                    waitForVmEvent = false
                    currentGoToEmailSent()
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

    EditNameFormContent(
        givenName = viewModel.uiState.chosenName,
        familyName = viewModel.uiState.familyName,
        inProgress = viewModel.uiState.inProgress,
        padding = it,
        onUpdateName = {
            waitForVmEvent = true
            viewModel.updateName()
        },
        onGivenNameChange = viewModel::onChosenNameChange,
        onFamilyNameChange = viewModel::onFamilyNameChange,
        goBack = goBack,
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun EditNameFormContent(
    givenName: String,
    familyName: String,
    inProgress: Boolean,
    padding: PaddingValues = PaddingValues(),
    onGivenNameChange: (String) -> Unit = {},
    onFamilyNameChange: (String) -> Unit = {},
    onUpdateName: () -> Unit = {},
    goBack: () -> Unit,
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween
) {
    val isGivenNameValid by remember(givenName) {
        derivedStateOf { givenName.isNotBlank() }
    }
    val isFamilyNameValid by remember(familyName) {
        derivedStateOf { familyName.isNotBlank() }
    }

    val submitEnable by remember(isGivenNameValid, isFamilyNameValid) {
        derivedStateOf { isGivenNameValid && isFamilyNameValid }
    }
    val isKeyboardOpen by rememberUpdatedState(WindowInsets.isImeVisible)
    Column(
        horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }
        AnimatedVisibility(
            !isKeyboardOpen, Modifier.fillMaxWidth()
        ) {
            TwoColorTitle(
                firstPart = stringResource(R.string.EditName_Title_Edit_COPY),
                secondPart = "stringResource(R.string.EditName_Title_FullName_COPY)"
            )
            Spacer(Modifier.height(12.dp))
        }
        if (inProgress) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }

        Text(
            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
            text = stringResource(R.string.Email_Info_COPY),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = givenName,
            isError = !isGivenNameValid,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            onValueChange = onGivenNameChange,
            label = { Text(stringResource(R.string.Login_GivenName_COPY)) },
            placeholder = { Text(stringResource(R.string.Login_GivenNamePlaceholder_COPY)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = familyName,
            isError = !isFamilyNameValid,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            onValueChange = onFamilyNameChange,
            label = { Text(stringResource(R.string.Login_FamilyName_COPY)) },
            placeholder = { Text(stringResource(R.string.Login_FamilyNamePlaceholder_COPY)) },
            modifier = Modifier.fillMaxWidth()
        )
        LaunchedEffect(focusRequester) {
            awaitFrame()
            focusRequester.requestFocus()
        }
    }
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
            .padding(bottom = 24.dp),
    ) {
        SecondaryButton(
            modifier = Modifier.widthIn(min = 140.dp),
            text = stringResource(R.string.Email_Cancel_COPY),
            onClick = goBack,
        )
        PrimaryButton(
            modifier = Modifier.widthIn(min = 140.dp),
            text = stringResource(R.string.Email_Save_COPY),
            onClick = onUpdateName,
            buttonTextColor = Color.White,
            enabled = submitEnable,
        )
    }
}


@Preview
@Composable
private fun PreviewEditNameFormContent() = EduidAppAndroidTheme {
    EditNameFormContent(
        givenName = "Vetinari",
        familyName = "Lord",
        inProgress = true,
        onGivenNameChange = {},
        goBack = { },
    )
}