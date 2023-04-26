package nl.eduid.screens.homepage

import androidx.compose.runtime.Composable
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
    goToConfirmDeactivation: (String) -> Unit,
    onGoToRequestEduIdAccount: () -> Unit,
) {
    when (viewModel.isEnrolledState) {
        IsEnrolled.Unknown -> SplashScreen()
        IsEnrolled.No -> HomePageNoAccountContent(
            viewModel = viewModel,
            onGoToScan = onEnrollWithQR,
            onGoToRequestEduId = onGoToRequestEduIdAccount,
            onGoToSignIn = launchOAuth,
            onGoToRegistrationPinSetup = goToRegistrationPinSetup,
            onGoToConfirmDeactivation = goToConfirmDeactivation,
        )

        IsEnrolled.Yes -> HomePageWithAccountContent(
            viewModel = viewModel,
            onActivityClicked = onActivityClicked,
            onPersonalInfoClicked = onPersonalInfoClicked,
            onSecurityClicked = onSecurityClicked,
            onScanForAuthorization = onScanForAuthorization,
            launchOAuth = launchOAuth
        )
    }
}