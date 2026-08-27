package nl.eduid.screens.changephone

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.eduid.R
import nl.eduid.screens.biometric.BiometricSignIn
import nl.eduid.screens.biometric.SignInWithBiometricsContract
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.theme.ColorMain_Green_400
import nl.eduid.ui.theme.ColorScale_Gray_Black
import nl.eduid.ui.theme.EduidAppAndroidTheme
import org.tiqr.data.util.extension.biometricUsable

/**
 * Explains that the user must confirm their identity before changing the SMS recovery number. Confirming starts a
 * tiqr authentication, completed with biometrics when available, otherwise (or when biometrics fail) with the PIN.
 */
@Composable
fun ChangePhoneExplanationScreen(
    viewModel: ChangePhoneAuthViewModel,
    goBack: () -> Unit,
    goToVerifyPin: () -> Unit,
    goToRequestCode: () -> Unit,
) = EduIdTopAppBar(onBackClicked = goBack) { padding ->
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentGoToVerifyPin by rememberUpdatedState(goToVerifyPin)
    val currentGoToRequestCode by rememberUpdatedState(goToRequestCode)

    val launchBiometricSignIn = rememberLauncherForActivityResult(SignInWithBiometricsContract()) { result ->
        when (result) {
            is BiometricSignIn.Success -> viewModel.completeWithBiometric()
            is BiometricSignIn.Failed -> currentGoToVerifyPin()
        }
    }

    uiState.errorData?.let { errorData ->
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = viewModel::dismissError,
        )
    }

    LaunchedEffect(uiState.verifyWith) {
        when (uiState.verifyWith) {
            VerificationMethod.BIOMETRIC -> launchBiometricSignIn.launch(Unit)
            VerificationMethod.PIN -> currentGoToVerifyPin()
            null -> return@LaunchedEffect
        }
        viewModel.clearVerifyWith()
    }

    LaunchedEffect(uiState.isVerified) {
        if (uiState.isVerified != null) {
            viewModel.clearVerified()
            currentGoToRequestCode()
        }
    }

    ChangePhoneExplanationContent(
        inProgress = uiState.inProgress,
        padding = padding,
        onBack = goBack,
        onChange = { viewModel.startAuthentication(canUseBiometric = context.biometricUsable()) },
    )
}

@Composable
private fun ChangePhoneExplanationContent(
    inProgress: Boolean,
    padding: PaddingValues = PaddingValues(),
    onBack: () -> Unit = {},
    onChange: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .navigationBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.ChangeSMSRecovery_Title_COPY),
            style = MaterialTheme.typography.titleLarge.copy(color = ColorMain_Green_400, textAlign = TextAlign.Start),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(18.dp))
        if (inProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(18.dp))
        }
        Text(
            text = stringResource(R.string.ChangeSMSRecovery_Description_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(color = ColorScale_Gray_Black, textAlign = TextAlign.Start),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    Spacer(Modifier.height(18.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth(),
    ) {
        SecondaryButton(
            enabled = !inProgress,
            text = stringResource(R.string.ChangeSMSRecovery_Button_Back_COPY),
            modifier = Modifier.widthIn(min = 140.dp),
            onClick = onBack,
        )
        PrimaryButton(
            enabled = !inProgress,
            text = stringResource(R.string.ChangeSMSRecovery_Button_Change_COPY),
            modifier = Modifier.widthIn(min = 140.dp),
            onClick = onChange,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_ChangePhoneExplanationContent() = EduidAppAndroidTheme {
    ChangePhoneExplanationContent(inProgress = false)
}

@Preview(showBackground = true)
@Composable
private fun Preview_ChangePhoneExplanationContent_InProgress() = EduidAppAndroidTheme {
    ChangePhoneExplanationContent(inProgress = true)
}
