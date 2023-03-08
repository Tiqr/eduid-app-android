package nl.eduid.graphs

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import nl.eduid.screens.biometric.EnableBiometricScreen
import nl.eduid.screens.biometric.EnableBiometricViewModel
import nl.eduid.screens.firsttimedialog.FirstTimeDialogScreen
import nl.eduid.screens.homepage.HomePageScreen
import nl.eduid.screens.homepage.HomePageViewModel
import nl.eduid.screens.oauth.OAuthScreen
import nl.eduid.screens.oauth.OAuthViewModel
import nl.eduid.screens.personalinfo.PersonalInfoScreen
import nl.eduid.screens.personalinfo.PersonalInfoViewModel
import nl.eduid.screens.pinsetup.RegistrationPinSetupScreen
import nl.eduid.screens.pinsetup.RegistrationPinSetupViewModel
import nl.eduid.screens.requestiddetails.RequestEduIdFormScreen
import nl.eduid.screens.requestiddetails.RequestEduIdFormViewModel
import nl.eduid.screens.requestidlinksent.RequestEduIdEmailSentScreen
import nl.eduid.screens.requestidpin.RequestIdConfirmPhoneNumber
import nl.eduid.screens.requestidpin.RequestIdPinViewModel
import nl.eduid.screens.requestidrecovery.RequestIdRecoveryScreen
import nl.eduid.screens.requestidrecovery.RequestIdRecoveryViewModel
import nl.eduid.screens.requestidstart.RequestEduIdStartScreen
import nl.eduid.screens.scan.ScanScreen
import nl.eduid.screens.scan.StatelessScanViewModel
import nl.eduid.screens.start.StartScreen

@Composable
fun MainGraph(navController: NavHostController, homePageViewModel: HomePageViewModel) = NavHost(
    navController = navController, route = Graph.MAIN, startDestination = Graph.HOME_PAGE
) {
    composable(Graph.HOME_PAGE) {
        HomePageScreen(viewModel = homePageViewModel,
            onScanForAuthorization = { /*QR authorization for 3rd party*/ },
            onActivityClicked = { },
            onPersonalInfoClicked = { navController.navigate(Graph.PERSONAL_INFO) },
            onSecurityClicked = {},
            onEnrollWithQR = { navController.navigate(ExistingAccount.EnrollWithQR.route) },
            launchOAuth = { navController.navigate(OAuth.routeWithoutPhone) }
        ) {
            navController.navigate(
                Graph.REQUEST_EDU_ID_ACCOUNT
            )
        }
    }

    composable(ExistingAccount.EnrollWithQR.route) {
        val viewModel = hiltViewModel<StatelessScanViewModel>(it)
        ScanScreen(viewModel = viewModel,
            isRegistration = true,
            goBack = { navController.popBackStack() },
            goToRegistrationPinSetup = { challenge ->
                navController.navigate(
                    "${ExistingAccount.RegistrationPinSetup.route}/${
                        viewModel.encodeChallenge(
                            challenge
                        )
                    }"
                )
            },
            goToAuthentication = {})
    }

    composable(
        route = ExistingAccount.RegistrationPinSetup.routeWithArgs,
        arguments = ExistingAccount.RegistrationPinSetup.arguments
    ) { entry ->
        val viewModel = hiltViewModel<RegistrationPinSetupViewModel>(entry)
        RegistrationPinSetupScreen(viewModel = viewModel,
            closePinSetupFlow = { navController.popBackStack() },
            goToBiometricEnable = { challenge, pin ->
                navController.navigate(
                    WithChallenge.EnableBiometric.buildRouteForEnrolment(
                        encodedChallenge = viewModel.encodeChallenge(challenge), pin = pin
                    )
                ) {
                    popUpTo(navController.graph.findStartDestination().id)
                }
            },
            onRegistrationDone = {
                navController.navigate(OAuth.routeWithPhone) {
                    popUpTo(navController.graph.findStartDestination().id)
                }
            })
    }
    composable(
        route = WithChallenge.EnableBiometric.routeWithArgs, arguments = WithChallenge.arguments
    ) { entry ->
        val viewModel = hiltViewModel<EnableBiometricViewModel>(entry)
        val isEnroll = entry.arguments?.getBoolean(WithChallenge.isEnrolmentArg, true) ?: true
        val authRoute = if (isEnroll) OAuth.routeWithPhone else OAuth.routeWithoutPhone
        EnableBiometricScreen(
            viewModel = viewModel,
            goToOauth = { navController.goToWithPopCurrent(destination = authRoute) }
        ) { navController.popBackStack() }
    }
    composable(route = OAuth.routeWithArgs, arguments = OAuth.arguments) { entry ->
        val viewModel = hiltViewModel<OAuthViewModel>(entry)
        val isEnroll = entry.arguments?.getBoolean(OAuth.withPhoneConfirmArg, true) ?: true
        OAuthScreen(viewModel = viewModel, continueWith = {
            if (isEnroll) {
                navController.goToWithPopCurrent(destination = PhoneNumberRecovery.RequestCode.route)
            } else {
                navController.goToWithPopCurrent(destination = Graph.MAIN)
            }
        }) {
            navController.popBackStack()
        }
    }

    composable(Graph.REQUEST_EDU_ID_ACCOUNT) {
        RequestEduIdStartScreen(requestId = { navController.navigate(Graph.REQUEST_EDU_ID_FORM) },
            onBackClicked = { navController.popBackStack() })
    }
    composable(Graph.REQUEST_EDU_ID_FORM) {
        val viewModel = hiltViewModel<RequestEduIdFormViewModel>(it)
        RequestEduIdFormScreen(viewModel = viewModel,
            goToEmailLinkSent = { email -> navController.goToEmailSent(email) },
            onBackClicked = { navController.popBackStack() })
    }

    composable(
        route = RequestEduIdLinkSent.routeWithArgs, arguments = RequestEduIdLinkSent.arguments
    ) { entry ->
        RequestEduIdEmailSentScreen(userEmail = RequestEduIdLinkSent.decodeFromEntry(entry),
            requestId = { navController.navigate(PhoneNumberRecovery.RequestCode.route) },
            onBackClicked = { navController.popBackStack() })
    }

    composable(
        PhoneNumberRecovery.RequestCode.route,
    ) {
        val viewModel = hiltViewModel<RequestIdRecoveryViewModel>(it)
        RequestIdRecoveryScreen(
            onVerifyPhoneNumberClicked = { navController.navigate(PhoneNumberRecovery.ConfirmCode.route) },
            onBackClicked = { navController.popBackStack() },
            viewModel = viewModel,
        )
    }

    composable(PhoneNumberRecovery.ConfirmCode.route) {
        val viewModel = hiltViewModel<RequestIdPinViewModel>(it)
        RequestIdConfirmPhoneNumber(viewModel = viewModel,
            onCodeVerified = { navController.navigate(Graph.START) },
            goBack = { navController.popBackStack() })
    }

    composable(Graph.START) {
        StartScreen(
            onNext = { navController.navigate(Graph.HOME_PAGE) },
        )
    }

    composable(Graph.FIRST_TIME_DIALOG) {
        FirstTimeDialogScreen()
    }

    composable(Graph.PERSONAL_INFO) {
        val viewModel = hiltViewModel<PersonalInfoViewModel>(it)
        PersonalInfoScreen(
            viewModel = viewModel,
            onNameClicked = { },
            onEmailClicked = { },
            onRoleClicked = { },
            onInstitutionClicked = { },
            goBack = { navController.popBackStack() },
        )
    }
}

private fun NavController.goToEmailSent(email: String) = navigate(
    RequestEduIdLinkSent.routeWithEmail(email)
)

private fun NavController.goToWithPopCurrent(destination: String) {
    val currentRouteId = currentDestination?.id ?: 0
    navigate(destination) {
        popUpTo(currentRouteId) { inclusive = true }
    }
}
