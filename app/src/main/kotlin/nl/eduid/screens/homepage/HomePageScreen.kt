package nl.eduid.screens.homepage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import nl.eduid.screens.splash.SplashScreen

@Composable
fun HomePageScreen(
    viewModel: HomePageViewModel,
    onScanForAuthorization: () -> Unit,
    onActivityClicked: () -> Unit,
    onPersonalInfoClicked: () -> Unit,
    onSecurityClicked: () -> Unit,
    onEnrollWithQR: () -> Unit,
    launchOAuth: () -> Unit,
    onCreateEduIdAccount: () -> Unit,
) {
    val isAuthorizedForDataAccess by viewModel.isAuthorizedForDataAccess.observeAsState(false)
    val uiState by viewModel.uiState.observeAsState(UiState())

    when (uiState.isEnrolled) {
        IsEnrolled.Unknown -> SplashScreen()
        IsEnrolled.No -> HomePageNoAccountContent(
            onScan = onEnrollWithQR,
            onRequestEduId = onCreateEduIdAccount,
            onSignIn = launchOAuth
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