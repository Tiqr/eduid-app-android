package nl.eduid.screens.changephone

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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PinInputField
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.theme.ColorMain_Green_400
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun ChangePhoneVerifyPinScreen(
    viewModel: ChangePhoneAuthViewModel,
    goBack: () -> Unit,
    abortFlow: () -> Unit,
    goToRequestCode: () -> Unit,
) = EduIdTopAppBar(onBackClicked = goBack) { padding ->
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentGoToRequestCode by rememberUpdatedState(goToRequestCode)
    var pinValue by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState.pinRejections) {
        if (uiState.pinRejections > 0) pinValue = ""
    }

    uiState.errorData?.let { errorData ->
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = {
                val shouldAbort = uiState.abortOnErrorDismiss
                viewModel.dismissError()
                if (shouldAbort) abortFlow()
            },
        )
    }

    LaunchedEffect(uiState.isVerified) {
        if (uiState.isVerified != null) {
            viewModel.clearVerified()
            currentGoToRequestCode()
        }
    }

    ChangePhoneVerifyPinContent(
        pinValue = pinValue,
        inProgress = uiState.inProgress,
        padding = padding,
        onPinChange = { pinValue = it },
        onCancel = goBack,
        onSubmit = { viewModel.completeWithPin(pinValue) },
    )
}

@Composable
private fun ChangePhoneVerifyPinContent(
    pinValue: String,
    inProgress: Boolean,
    padding: PaddingValues = PaddingValues(),
    onPinChange: (String) -> Unit = {},
    onCancel: () -> Unit = {},
    onSubmit: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.auth_pin_title),
            style = MaterialTheme.typography.titleLarge.copy(color = ColorMain_Green_400, textAlign = TextAlign.Start),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(36.dp))
        if (inProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        PinInputField(
            label = stringResource(org.tiqr.data.R.string.auth_pin_subtitle),
            pinCode = pinValue,
            isPinInvalid = false,
            isInputEnabled = !inProgress,
            modifier = Modifier.fillMaxWidth(),
            onPinChange = onPinChange,
            submitPin = onSubmit,
        )
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
            text = stringResource(R.string.Button_Cancel_COPY),
            onClick = onCancel,
            enabled = !inProgress,
        )
        PrimaryButton(
            modifier = Modifier.widthIn(min = 140.dp),
            text = stringResource(R.string.Button_OK_COPY),
            onClick = onSubmit,
            enabled = !inProgress && pinValue.isNotEmpty(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_ChangePhoneVerifyPinContent() = EduidAppAndroidTheme {
    ChangePhoneVerifyPinContent(pinValue = "12", inProgress = false)
}

@Preview(showBackground = true)
@Composable
private fun Preview_ChangePhoneVerifyPinContent_InProgress() = EduidAppAndroidTheme {
    ChangePhoneVerifyPinContent(pinValue = "12345", inProgress = true)
}
