package nl.eduid

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import nl.eduid.screens.biometric.EnableBiometricScreen
import nl.eduid.screens.biometric.EnableBiometricViewModel
import nl.eduid.screens.enroll.EnrollScreen
import nl.eduid.screens.firsttimedialog.FirstTimeDialogScreen
import nl.eduid.screens.homepage.HomePageScreen
import nl.eduid.screens.homepage.HomePageViewModel
import nl.eduid.screens.login.LoginScreen
import nl.eduid.screens.login.LoginViewModel
import nl.eduid.screens.oauth.OAuthScreen
import nl.eduid.screens.oauth.OAuthViewModel
import nl.eduid.screens.personalinfo.PersonalInfoScreen
import nl.eduid.screens.personalinfo.PersonalInfoViewModel
import nl.eduid.screens.pinsetup.RegistrationPinSetupScreen
import nl.eduid.screens.pinsetup.RegistrationPinSetupViewModel
import nl.eduid.screens.requestiddetails.RequestIdDetailsScreen
import nl.eduid.screens.requestiddetails.RequestIdDetailsViewModel
import nl.eduid.screens.requestidlinksent.RequestIdLinkSentScreen
import nl.eduid.screens.requestidpin.RequestIdPinScreen
import nl.eduid.screens.requestidpin.RequestIdPinViewModel
import nl.eduid.screens.requestidrecovery.RequestIdRecoveryScreen
import nl.eduid.screens.requestidrecovery.RequestIdRecoveryViewModel
import nl.eduid.screens.requestidstart.RequestIdStartScreen
import nl.eduid.screens.scan.ScanScreen
import nl.eduid.screens.scan.StatelessScanViewModel
import nl.eduid.screens.splash.SplashScreen
import nl.eduid.screens.splash.SplashViewModel
import nl.eduid.screens.start.StartScreen

@Composable
fun MainGraph(navController: NavHostController) = NavHost(
    navController = navController, route = Graph.MAIN, startDestination = Graph.SPLASH
) {
    composable(Graph.SPLASH) {
        val viewModel = hiltViewModel<SplashViewModel>(it)
        SplashScreen(viewModel = viewModel, onFirstTimeUser = {
            navController.navigate(Graph.ENROLL) {
                popUpTo(Graph.SPLASH) {
                    inclusive = true
                }
            }
        }) {
            navController.navigate(Graph.HOME_PAGE) {
                popUpTo(Graph.SPLASH) {
                    inclusive = true
                }
            }
        }
    }
    composable(Graph.ENROLL) {
        EnrollScreen(onLogin = { navController.navigate(Graph.LOGIN) },
            onScan = { navController.navigate(Graph.SCAN_REGISTRATION) },
            onRequestEduId = { navController.navigate(Graph.REQUEST_EDU_ID_START) })
    }
    composable(Graph.LOGIN) {
        val viewModel = hiltViewModel<LoginViewModel>(it)
        LoginScreen(viewModel = viewModel,
            onLoginDone = {},
            goBack = { navController.popBackStack() })
    }
    composable(Graph.SCAN_REGISTRATION) {
        val viewModel = hiltViewModel<StatelessScanViewModel>(it)
        ScanScreen(viewModel = viewModel,
            isRegistration = true,
            goBack = { navController.popBackStack() },
            goToRegistrationPinSetup = { challenge ->
                navController.navigate(
                    RegistrationPinSetup.buildRouteWithEncodedChallenge(
                        encodedChallenge = viewModel.encodeChallenge(
                            challenge
                        )
                    )
                )
            },
            goToAuthentication = {})
    }
    composable(
        route = RegistrationPinSetup.routeWithArgs, arguments = RegistrationPinSetup.arguments
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
                    popUpTo(Graph.ENROLL) {
                        inclusive = true
                    }
                }
            }) { challenge, pin ->
            navController.navigate(
                WithChallenge.OAuth.buildRouteForEnrolment(
                    encodedChallenge = viewModel.encodeChallenge(challenge), pin = pin
                )
            ) {
                popUpTo(Graph.ENROLL) {
                    inclusive = true
                }
            }
        }
    }
    composable(
        route = WithChallenge.EnableBiometric.routeWithArgs, arguments = WithChallenge.arguments
    ) { entry ->
        val viewModel = hiltViewModel<EnableBiometricViewModel>(entry)
        EnableBiometricScreen(viewModel = viewModel) {
            navController.navigate(
                WithChallenge.OAuth.buildRouteForEnrolment(
                    encodedChallenge = viewModel.encodeChallenge(challenge), pin = pin
                )
            ) {
                popUpTo(Graph.ENROLL) {
                    inclusive = true
                }
            }
        }
    }
    composable(
        route = WithChallenge.OAuth.routeWithArgs, arguments = WithChallenge.arguments
    ) { entry ->
        val viewModel = hiltViewModel<OAuthViewModel>(entry)
        OAuthScreen(viewModel = viewModel, goToBiometricEnable = { challenge, pin ->
            navController.navigate(
                WithChallenge.EnableBiometric.buildRouteForEnrolment(
                    encodedChallenge = viewModel.encodeChallenge(challenge), pin = pin
                )
            )
        }) {
            navController.navigate(Graph.HOME_PAGE) {
                popUpTo(Graph.SCAN_REGISTRATION) {
                    inclusive = true
                }
            }
        }
    }
    composable(Graph.REQUEST_EDU_ID_START) {
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
            requestId = { navController.navigate(Graph.REQUEST_EDU_ID_RECOVERY) },
            onBackClicked = { navController.popBackStack() })
    }

    composable(Graph.REQUEST_EDU_ID_RECOVERY) {
        val viewModel = hiltViewModel<RequestIdRecoveryViewModel>(it)
        RequestIdRecoveryScreen(
            onVerifyPhoneNumberClicked = { navController.navigate(Graph.REQUEST_EDU_ID_PIN) },
            onBackClicked = { navController.popBackStack() },
            viewModel = viewModel,
        )
    }

    composable(Graph.REQUEST_EDU_ID_PIN) {
        val viewModel = hiltViewModel<RequestIdPinViewModel>(it)
        RequestIdPinScreen(viewModel = viewModel,
            onPinVerified = { navController.navigate(Graph.START) },
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

    composable(Graph.HOME_PAGE) {
        val viewModel = hiltViewModel<HomePageViewModel>(it)
        HomePageScreen(
            viewModel = viewModel,
            onActivityClicked = { },
            onPersonalInfoClicked = { navController.navigate(Graph.PERSONAL_INFO) },
            onSecurityClicked = {},
        )
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