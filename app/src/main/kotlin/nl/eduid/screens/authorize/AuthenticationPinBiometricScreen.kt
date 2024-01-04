package nl.eduid.screens.authorize

import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import biometricUsable
import nl.eduid.R
import nl.eduid.screens.biometric.BiometricSignIn
import nl.eduid.screens.biometric.SignInWithBiometricsContract
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PinInputField
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGreen
import org.tiqr.data.model.AuthenticationChallenge
import org.tiqr.data.model.AuthenticationCompleteFailure
import org.tiqr.data.model.ChallengeCompleteFailure
import org.tiqr.data.model.ChallengeCompleteResult
import timber.log.Timber

@Composable
fun AuthenticationPinBiometricScreen(
    viewModel: EduIdAuthenticationViewModel,
    goToAuthenticationComplete: (AuthenticationChallenge?, String) -> Unit,
    onCancel: () -> Unit,
) = EduIdTopAppBar(
    withBackIcon = false
) {
    val authChallenge by viewModel.challenge.observeAsState(null)
    val challengeComplete by viewModel.challengeComplete.observeAsState(null)
    val context = LocalContext.current

    AuthenticationPinBiometricContent(
        shouldPromptBiometric = context.biometricUsable() && authChallenge?.identity?.biometricInUse == true,
        challengeComplete = challengeComplete,
        padding = it,
        onBiometricResult = { biometricSignIn ->
            viewModel.authenticateWithBiometric(biometricSignIn)
        },
        submitPin = { pin ->
            viewModel.authenticateWithPin(pin)
        },
        onCancel = onCancel,
        goToAuthenticationComplete = { pin ->
            goToAuthenticationComplete(authChallenge, pin)
        },
        clearCompleteChallenge = viewModel::clearCompleteChallenge,
        goHomeOnFail = onCancel,
    )
}

@Composable
private fun AuthenticationPinBiometricContent(
    shouldPromptBiometric: Boolean,
    challengeComplete: ChallengeCompleteResult<ChallengeCompleteFailure>? = null,
    isPinInvalid: Boolean = false,
    padding: PaddingValues = PaddingValues(),
    onBiometricResult: (BiometricSignIn) -> Unit = {},
    submitPin: (String) -> Unit = {},
    onCancel: () -> Unit = {},
    goToAuthenticationComplete: (String) -> Unit = {},
    clearCompleteChallenge: () -> Unit = {},
    goHomeOnFail: () -> Unit = {},
) {
    var isCheckingSecret by rememberSaveable { mutableStateOf(false) }
    var pinValue by rememberSaveable { mutableStateOf("") }
    var shouldShowKeyboard by remember { mutableStateOf(!shouldPromptBiometric) }
    val owner = LocalLifecycleOwner.current

    if (isCheckingSecret && challengeComplete != null) {
        when (challengeComplete) {
            is ChallengeCompleteResult.Failure -> {
                val failure = if (challengeComplete.failure is AuthenticationCompleteFailure) {
                    challengeComplete.failure as AuthenticationCompleteFailure
                } else {
                    return
                }
                when (failure.reason) {
                    AuthenticationCompleteFailure.Reason.UNKNOWN,
                    AuthenticationCompleteFailure.Reason.CONNECTION,
                    -> {
                        Timber.e("This should be a fallback to OTP")
                        TODO()
                    }

                    AuthenticationCompleteFailure.Reason.INVALID_RESPONSE -> {
                        val remaining = failure.remainingAttempts
                        if (remaining == null || remaining > 0) {
                            pinValue = ""
                        }
                        AlertDialogWithSingleButton(title = failure.title,
                            explanation = failure.message,
                            buttonLabel = stringResource(R.string.Button_OK_COPY),
                            onDismiss = {
                                if (remaining != null && remaining == 0) {
                                    goHomeOnFail()
                                }
                                isCheckingSecret = false
                                clearCompleteChallenge()
                            })
                    }

                    else -> {
                        AlertDialogWithSingleButton(
                            title = failure.title,
                            explanation = failure.message,
                            buttonLabel = stringResource(R.string.Button_OK_COPY),
                            onDismiss = {
                                isCheckingSecret = false
                                clearCompleteChallenge()
                            }
                        )
                    }
                }
            }

            ChallengeCompleteResult.Success -> {
                val currentGoToAuthenticationComplete by rememberUpdatedState(
                    goToAuthenticationComplete
                )
                LaunchedEffect(owner) {
                    isCheckingSecret = false
                    currentGoToAuthenticationComplete(pinValue)
                }
            }
        }
    }

    if (shouldPromptBiometric) {
        var hasAutoRequestedBiometrics by remember {
            mutableStateOf(false)
        }
        val launchBiometricSignIn = rememberLauncherForActivityResult(
            contract = SignInWithBiometricsContract(),
        ) { result ->
            when (result) {
                is BiometricSignIn.Failed -> shouldShowKeyboard = true
                is BiometricSignIn.Success -> {
                    isCheckingSecret = true
                    onBiometricResult(result)
                }
            }
        }
        if (!hasAutoRequestedBiometrics) {
            SideEffect {
                hasAutoRequestedBiometrics = true
                launchBiometricSignIn.launch(Unit)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start, color = TextGreen
                ),
                text = stringResource(R.string.auth_pin_title),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(36.dp))
            PinInputField(label = stringResource(org.tiqr.data.R.string.auth_pin_subtitle),
                pinCode = pinValue,
                isPinInvalid = isPinInvalid,
                shouldShowKeyboard = shouldShowKeyboard,
                modifier = Modifier.fillMaxWidth(),
                onPinChange = { newValue -> pinValue = newValue },
                submitPin = {
                    isCheckingSecret = true
                    submitPin(pinValue)
                })

        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            SecondaryButton(
                modifier = Modifier.widthIn(min = 140.dp),
                text = stringResource(R.string.Button_Cancel_COPY),
                onClick = onCancel,
            )
            PrimaryButton(
                modifier = Modifier.widthIn(min = 140.dp),
                text = stringResource(R.string.Button_OK_COPY),
                onClick = {
                    isCheckingSecret = true
                    submitPin(pinValue)
                },
            )
        }
    }
}

@Preview
@Composable
private fun PreviewAuthorizePinBiometricScreen() = EduidAppAndroidTheme {
    AuthenticationPinBiometricContent(
        shouldPromptBiometric = false,
        isPinInvalid = false,
    )
}