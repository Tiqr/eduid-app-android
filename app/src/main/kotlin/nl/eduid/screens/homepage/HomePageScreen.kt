package nl.eduid.screens.homepage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import nl.eduid.screens.splash.SplashScreen
import org.tiqr.data.model.EnrollmentChallenge

@Composable
fun HomePageScreen(
    viewModel: HomePageViewModel,
    onScanForAuthorization: () -> Unit,
    onActivityClicked: () -> Unit,
    onPersonalInfoClicked: () -> Unit,
    onSecurityClicked: () -> Unit,
    onEnrollWithQR: () -> Unit,
    launchOAuth: () -> Unit,
    goToRegistrationPinSetup: (EnrollmentChallenge) -> Unit,
    confirmDeactivation: (String) -> Unit,
    onCreateEduIdAccount: () -> Unit,
) {
    val isAuthorizedForDataAccess by viewModel.isAuthorizedForDataAccess.observeAsState(false)
    val uiState by viewModel.uiState.observeAsState(UiState())
    var waitingForVmEvent by rememberSaveable { mutableStateOf(false) }
    val owner = LocalLifecycleOwner.current

    uiState.deactivateFor?.let {
        val currentConfirmDeactivation by rememberUpdatedState(confirmDeactivation)
        if (waitingForVmEvent) {
            LaunchedEffect(owner) {
                currentConfirmDeactivation(it.phoneNumber)
                viewModel.clearDeactivation()
                waitingForVmEvent = false
            }
        }
    }

    when (uiState.isEnrolled) {
        IsEnrolled.Unknown -> SplashScreen()
        IsEnrolled.No -> HomePageNoAccountContent(
            isAuthorizedForDataAccess = isAuthorizedForDataAccess,
            uiState = uiState,
            onScan = onEnrollWithQR,
            onRequestEduId = onCreateEduIdAccount,
            onSignIn = launchOAuth,
            onStartEnrolment = viewModel::startEnrollmentAfterSignIn,
            goToRegistrationPinSetup = { challenge ->
                goToRegistrationPinSetup(challenge)
                viewModel.clearCurrentChallenge()
            },
            dismissError = viewModel::dismissError,
            clearPreEnrollCheck = viewModel::clearPreEnrollCheck,
            handlePreEnrollCheck = {
                if (it is PreEnrollCheck.DeactivateExisting) {
                    waitingForVmEvent = true
                    viewModel.handleDeactivationRequest()
                }
                viewModel.clearPreEnrollCheck()
            }
        )

        IsEnrolled.Yes -> HomePageWithAccountContent(
            isAuthorizedForDataAccess = isAuthorizedForDataAccess,
            shouldPromptAuthorization = uiState.promptForAuth,
            onActivityClicked = onActivityClicked,
            onPersonalInfoClicked = onPersonalInfoClicked,
            onSecurityClicked = onSecurityClicked,
            onScanForAuthorization = onScanForAuthorization,
            launchOAuth = launchOAuth,
            promptAuthorization = viewModel::triggerPromptForAuth,
            clearAuth = viewModel::clearPromptForAuthTrigger
        )
    }
}