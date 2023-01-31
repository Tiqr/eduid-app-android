package nl.eduid.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.screens.biometric.Biometricks
import nl.eduid.screens.biometric.SignInWithBiometricsContract
import nl.eduid.ui.PinInputField
import nl.eduid.ui.PrimaryButton
import org.tiqr.data.viewmodel.AuthenticationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginDone: () -> Unit,
    loginState: LoginState = rememberLoginState(
        viewModel = viewModel, onLoginDone = onLoginDone
    ),
    goBack: () -> Unit,
) = Scaffold(
    snackbarHost = { SnackbarHost(hostState = (loginState.snackbarHostState)) },
    topBar = {
        CenterAlignedTopAppBar(
            modifier = Modifier.padding(top = 52.dp),
            navigationIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_top_scan),
                    contentDescription = stringResource(R.string.button_scan),
                    modifier = Modifier
                        .size(width = 32.dp, height = 32.dp)
                        .clickable {

                        },
                    alignment = Alignment.Center
                )
            },
            title = {
                Image(
                    painter = painterResource(R.drawable.ic_top_logo),
                    contentDescription = "",
                    modifier = Modifier.size(width = 122.dp, height = 46.dp),
                    alignment = Alignment.Center
                )
            },
        )
    },
    modifier = Modifier.systemBarsPadding(),
) { paddingValues ->
    Column(
        Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(horizontal = 8.dp)
    ) {

        val launchBiometricSignIn = rememberLauncherForActivityResult(
            contract = SignInWithBiometricsContract(),
        ) { result ->
            loginState.onBiometricsResult(result)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = stringResource(R.string.auth_pin_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.login_access_description),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            PinInputField(
                label = null,
                pinCode = loginState.pinCode,
                onPinChange = { loginState.onPinChange(it) },
                submitPin = loginState::confirmPin,
                isPinInvalid = false
            )
            Spacer(modifier = Modifier.height(52.dp))
        }
        PrimaryButton(
            text = stringResource(R.string.button_ok),
            onClick = loginState::confirmPin,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(40.dp))
        LaunchedEffect(loginState.biometrics) {
            if (loginState.biometrics == Biometricks.Available) {
                launchBiometricSignIn.launch(Unit)
            }
        }
    }
}

//
//@Preview
//@Composable
//private fun Preview_LoginScreen() = EduidAppAndroidTheme {
//    LoginScreen() {}
//}