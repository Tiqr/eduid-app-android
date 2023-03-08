package nl.eduid.graphs

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
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
import nl.eduid.screens.requestiddetails.RequestIdDetailsScreen
import nl.eduid.screens.requestiddetails.RequestIdDetailsViewModel
import nl.eduid.screens.requestidlinksent.RequestIdLinkSentScreen
import nl.eduid.screens.requestidpin.RequestIdConfirmPhoneNumber
import nl.eduid.screens.requestidpin.RequestIdPinViewModel
import nl.eduid.screens.requestidrecovery.RequestIdRecoveryScreen
import nl.eduid.screens.requestidrecovery.RequestIdRecoveryViewModel
import nl.eduid.screens.requestidstart.RequestIdStartScreen
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
            onEnrollWithQR = { navController.navigate(ExistingAccount.EnrollWithQR.route) }) {
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
        EnableBiometricScreen(viewModel = viewModel, goToOauth = {
            val currentRouteId = navController.currentDestination?.id ?: 0
            navController.navigate(authRoute) {
                popUpTo(currentRouteId) { inclusive = true }
            }
        }) { navController.popBackStack() }
    }
    composable(route = OAuth.routeWithArgs, arguments = OAuth.arguments) { entry ->
        val viewModel = hiltViewModel<OAuthViewModel>(entry)
        val isEnroll = entry.arguments?.getBoolean(OAuth.withPhoneConfirmArg, true) ?: true
        OAuthScreen(viewModel = viewModel, continueWith = {
            val currentRouteId = navController.currentDestination?.id ?: 0
            if (isEnroll) {
                navController.navigate(PhoneNumberRecovery.RequestCode.route) {
                    popUpTo(currentRouteId) { inclusive = true }
                }
            } else {
                navController.navigate(Graph.MAIN) {
                    popUpTo(currentRouteId) { inclusive = true }
                }
            }
        }) {
            navController.popBackStack()
        }
    }

    composable(Graph.REQUEST_EDU_ID_ACCOUNT) {
        RequestIdStartScreen(requestId = { navController.navigate(Graph.REQUEST_EDU_ID_DETAILS) },
            onBackClicked = { navController.popBackStack() })
    }
    composable(Graph.REQUEST_EDU_ID_DETAILS) {
        val viewModel = hiltViewModel<RequestIdDetailsViewModel>(it)
        RequestIdDetailsScreen(viewModel = viewModel,
            requestId = { email -> navController.navigate(Graph.REQUEST_EDU_ID_LINK_SENT + "/" + email.ifBlank { "(no email provided)" }) },
            onBackClicked = { navController.popBackStack() })
    }

    composable(Graph.REQUEST_EDU_ID_LINK_SENT + "/{userId}") { backStackEntry ->
        RequestIdLinkSentScreen(userEmail = backStackEntry.arguments?.getString("userId")
            ?: "your email address",
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