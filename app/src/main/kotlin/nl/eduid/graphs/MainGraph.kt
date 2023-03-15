package nl.eduid.graphs

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import nl.eduid.screens.accountlinked.AccountLinkedScreen
import nl.eduid.screens.biometric.EnableBiometricScreen
import nl.eduid.screens.biometric.EnableBiometricViewModel
import nl.eduid.screens.created.RequestEduIdCreatedScreen
import nl.eduid.screens.deeplinks.DeepLinkScreen
import nl.eduid.screens.deeplinks.DeepLinkViewModel
import nl.eduid.screens.firsttimedialog.LinkAccountViewModel
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
import nl.eduid.screens.requestidpin.ConfirmCodeScreen
import nl.eduid.screens.requestidpin.ConfirmCodeViewModel
import nl.eduid.screens.requestidrecovery.PhoneRequestCodeScreen
import nl.eduid.screens.requestidrecovery.PhoneRequestCodeViewModel
import nl.eduid.screens.requestidstart.RequestEduIdStartScreen
import nl.eduid.screens.scan.ScanScreen
import nl.eduid.screens.scan.StatelessScanViewModel
import nl.eduid.screens.start.StartScreen
import org.tiqr.data.model.EnrollmentChallenge

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainGraph(
    navController: NavHostController,
    homePageViewModel: HomePageViewModel,
) = NavHost(
    navController = navController, startDestination = Graph.HOME_PAGE
) {

    //region HomePage
    composable(Graph.HOME_PAGE) {
        HomePageScreen(viewModel = homePageViewModel,
            onScanForAuthorization = { /*QR authorization for 3rd party*/ },
            onActivityClicked = { },
            onPersonalInfoClicked = { navController.navigate(Graph.PERSONAL_INFO) },
            onSecurityClicked = {},
            onEnrollWithQR = { navController.navigate(Account.ScanQR.route) },
            launchOAuth = { navController.navigate(OAuth.routeForAuthorization) }) {
            navController.navigate(
                Graph.REQUEST_EDU_ID_ACCOUNT
            )
        }
    }
    //endregion
    //region Scan
    composable(Account.ScanQR.route) {
        val viewModel = hiltViewModel<StatelessScanViewModel>(it)
        ScanScreen(viewModel = viewModel,
            isRegistration = true,
            goBack = { navController.popBackStack() },
            goToNext = { challenge ->
                val encodedChallenge = viewModel.encodeChallenge(challenge)
                if (challenge is EnrollmentChallenge) {
                    navController.goToWithPopCurrent(
                        "${Account.EnrollPinSetup.route}/$encodedChallenge"
                    )
                } else {
                    navController.goToWithPopCurrent(
                        "${Account.Authorize.route}/$encodedChallenge"
                    )
                }
            })
    }
    //endregion
    //region PinSetup
    composable(
        route = Account.EnrollPinSetup.routeWithArgs,
        arguments = Account.EnrollPinSetup.arguments,
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
                navController.navigate(OAuth.routeForEnrollment) {
                    popUpTo(navController.graph.findStartDestination().id)
                }
            })
    }
    //endregion
    //region Authorize
    composable(
        route = Account.Authorize.routeWithArgs,
        arguments = Account.Authorize.arguments,
    ) { entry ->
    }
    //endregion

    //region DeepLinks
    composable(
        route = Account.DeepLink.route, deepLinks = listOf(navDeepLink {
            uriPattern = Account.DeepLink.enrollPattern
            action = Intent.ACTION_VIEW
        }, navDeepLink {
            uriPattern = Account.DeepLink.authPattern
            action = Intent.ACTION_VIEW
        })
    ) { entry ->
        val viewModel = hiltViewModel<DeepLinkViewModel>(entry)
        DeepLinkScreen(viewModel = viewModel, goToNext = { challenge ->
            val encodedChallenge = viewModel.encodeChallenge(challenge)
            if (challenge is EnrollmentChallenge) {
                navController.goToWithPopCurrent("${Account.EnrollPinSetup.route}/$encodedChallenge")
            } else {
                navController.goToWithPopCurrent(
                    "${Account.Authorize.route}/$encodedChallenge"
                )
            }
        })
    }

    //endregion
    //region EnableBiometric-Conditional
    composable(
        route = WithChallenge.EnableBiometric.routeWithArgs, arguments = WithChallenge.arguments
    ) { entry ->
        val viewModel = hiltViewModel<EnableBiometricViewModel>(entry)
        val isEnroll = entry.arguments?.getBoolean(WithChallenge.isEnrolmentArg, true) ?: true
        val authRoute = if (isEnroll) OAuth.routeForEnrollment else OAuth.routeForAuthorization
        EnableBiometricScreen(viewModel = viewModel,
            goToOauth = { navController.goToWithPopCurrent(destination = authRoute) }) { navController.popBackStack() }
    }
    //endregion
    //region OAuth-Conditional
    composable(route = OAuth.routeWithArgs, arguments = OAuth.arguments) { entry ->
        val viewModel = hiltViewModel<OAuthViewModel>(entry)
        val nextStep = entry.arguments?.getString(OAuth.nextStepArg, OAuth.routeForEnrollment)
            ?: OAuth.routeForEnrollment
        ExampleAnimation {
            OAuthScreen(viewModel = viewModel, continueWith = {
                when (nextStep) {
                    OAuth.routeForEnrollment -> {
                        navController.goToWithPopCurrent(destination = PhoneNumberRecovery.RequestCode.route)
                    }
                    OAuth.routeForAuthorization -> {
                        navController.goToWithPopCurrent(destination = Graph.HOME_PAGE)
                    }
                    OAuth.routeForOAuth -> {
                        navController.popBackStack()
                    }
                }
            }) {
                navController.popBackStack()
            }
        }
    }
    //endregion
    //region CreateAccount
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
        RequestEduIdEmailSentScreen(
            onBackClicked = { navController.popBackStack() },
            userEmail = RequestEduIdLinkSent.decodeFromEntry(entry)
        )
    }
    composable(
        route = RequestEduIdCreated.routeWithArgs, deepLinks = listOf(navDeepLink {
            uriPattern = RequestEduIdCreated.uriPattern
        })
    ) { entry ->
        val isCreated = RequestEduIdCreated.decodeFromEntry(entry)
        RequestEduIdCreatedScreen(
            justCreated = isCreated,
            viewModel = homePageViewModel,
            goToOAuth = { navController.navigate(OAuth.routeForOAuth) },
            goToRegistrationPinSetup = { challenge ->
                navController.navigate(
                    "${Account.EnrollPinSetup.route}/${
                        homePageViewModel.encodeChallenge(
                            challenge
                        )
                    }"
                ) {
                    //Clear the entire flow for creating a new eduid account
                    popUpTo(Graph.REQUEST_EDU_ID_ACCOUNT)
                }
            },
        )
    }
    //endregion

    //region VerifyPhone-Recovery
    composable(
        PhoneNumberRecovery.RequestCode.route,
    ) {
        val viewModel = hiltViewModel<PhoneRequestCodeViewModel>(it)
        PhoneRequestCodeScreen(
            viewModel = viewModel,
            onBackClicked = { navController.popBackStack() },
        ) { phoneNumber ->
            navController.navigate(
                PhoneNumberRecovery.ConfirmCode.routeWithPhoneNumber(phoneNumber)
            )
        }
    }

    composable(
        route = PhoneNumberRecovery.ConfirmCode.routeWithArgs,
        arguments = PhoneNumberRecovery.ConfirmCode.arguments
    ) { entry ->
        val viewModel = hiltViewModel<ConfirmCodeViewModel>(entry)
        ConfirmCodeScreen(viewModel = viewModel,
            phoneNumber = PhoneNumberRecovery.ConfirmCode.decodeFromEntry(entry),
            goToStartScreen = {
                navController.navigate(Graph.START) {
                    //Flow for phone number recovery completed, remove from stack entirely
                    popUpTo(PhoneNumberRecovery.RequestCode.route) { inclusive = true }
                }
            }) { navController.popBackStack() }
    }
    //endregion

    //region Welcome-FirstTime
    composable(Graph.START) {
        StartScreen(
            onNext = { navController.goToWithPopCurrent(Graph.FIRST_TIME_DIALOG) },
        )
    }

    composable(Graph.FIRST_TIME_DIALOG) { entry ->
        val viewModel = hiltViewModel<LinkAccountViewModel>(entry)
        FirstTimeDialogScreen(viewModel = viewModel,
            goToAccountLinked = { navController.goToWithPopCurrent(AccountLinked.route) },
            skipThis = { navController.goToWithPopCurrent(Graph.HOME_PAGE) })
    }
    //endregion
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
    composable(
        route = AccountLinked.route,
        deepLinks = listOf(navDeepLink { uriPattern = AccountLinked.uriPattern })
    ) {
        val viewModel = hiltViewModel<PersonalInfoViewModel>(it)
        AccountLinkedScreen(
            viewModel = viewModel,
            continueToHome = { navController.goToWithPopCurrent(Graph.HOME_PAGE) },
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
