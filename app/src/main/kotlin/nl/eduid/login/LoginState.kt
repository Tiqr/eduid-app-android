package nl.eduid.login

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import nl.eduid.biometric.BiometricSignIn
import nl.eduid.biometric.Biometricks
import org.tiqr.data.model.SecretCredential

@Composable
fun rememberLoginState(
    viewModel: LoginViewModel,
    onLoginDone: () -> Unit,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): LoginState {
    val currentOnLoginDone by rememberUpdatedState(onLoginDone)
    return remember(viewModel, lifecycleOwner, context, coroutineScope) {
        LoginState(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            onLoginDone = currentOnLoginDone,
            lifecycleOwner = lifecycleOwner,
            coroutineScope = coroutineScope
        )
    }
}

@Stable
class LoginState(
    private val viewModel: LoginViewModel,
    val snackbarHostState: SnackbarHostState,
    val coroutineScope: CoroutineScope,
    onLoginDone: () -> Unit,
    lifecycleOwner: LifecycleOwner,
) {
    private var currentSnackbarJob: Job? = null
    var pinCode: String by mutableStateOf("")
    var biometrics: Biometricks by mutableStateOf(Biometricks.NoneEnrolled)

    init {
        biometrics = Biometricks.NoneEnrolled
//            if (viewModel.challenge.value?.identity?.biometricInUse == true) Biometricks.Available else Biometricks.NoneEnrolled
        viewModel.loginValid.observe(lifecycleOwner) { authenticateState ->
            currentSnackbarJob?.cancel()
            when (authenticateState) {
                true -> {
                    currentSnackbarJob = coroutineScope.launch {
                        snackbarHostState.showSnackbar("Authentication Success")
                    }
                    onLoginDone()
                }
                false -> {
                    currentSnackbarJob = coroutineScope.launch {
                        snackbarHostState.showSnackbar("Authentication Failed.")
                    }
                }
                else -> {}
            }
        }
    }

    fun confirmPin() = viewModel.isLoginValid(
        SecretCredential.pin(pinCode)
    )

    fun onBiometricsResult(biometricResult: BiometricSignIn) {
        when (biometricResult) {
            BiometricSignIn.Success -> {
                viewModel.isLoginValid(
                    SecretCredential.biometric()
                )
            }
            is BiometricSignIn.Failed -> {
                if (biometricResult.message.isNotEmpty()) {
                    currentSnackbarJob?.cancel()
                    currentSnackbarJob = coroutineScope.launch {
                        snackbarHostState.showSnackbar(biometricResult.message)
                    }
                }
            }
        }
    }

    fun onPinChange(newPin: String) {
        pinCode = newPin
    }
}