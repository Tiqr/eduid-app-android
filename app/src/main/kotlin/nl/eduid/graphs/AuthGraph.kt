package nl.eduid.graphs

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import nl.eduid.screens.authorize.AuthenticationCompletedScreen
import nl.eduid.screens.authorize.AuthenticationPinBiometricScreen
import nl.eduid.screens.authorize.EduIdAuthenticationViewModel
import nl.eduid.screens.authorize.RequestAuthenticationScreen
import nl.eduid.screens.onetimepassword.OneTimePasswordScreen
import org.tiqr.data.viewmodel.AuthenticationViewModel

fun NavGraphBuilder.authenticationFlow(navController: NavHostController) {
    navigation(
        route = Account.RequestAuthentication.routeWithArgs,
        arguments = Account.RequestAuthentication.arguments,
        startDestination = Graph.AUTH_GRAPH
    ) {
        composable(
            route = Graph.AUTH_GRAPH,
        ) { entry ->
            val viewModel = hiltViewModel<EduIdAuthenticationViewModel>(entry)
            RequestAuthenticationScreen(viewModel = viewModel, onLogin = { challenge ->
                if (challenge != null) {
                    val encodedChallenge = viewModel.encodeChallenge(challenge)
                    navController.goToWithPopCurrent("${Account.AuthenticationCheckSecret.route}/$encodedChallenge")
                }
            }) { navController.popBackStack() }
        }
        composable(
            route = Account.AuthenticationCheckSecret.routeWithArgs,
            arguments = Account.AuthenticationCheckSecret.arguments,
        ) { entry ->
            val viewModel = hiltViewModel<EduIdAuthenticationViewModel>(entry)
            AuthenticationPinBiometricScreen(viewModel = viewModel,
                goToAuthenticationComplete = { challenge, pin ->
                    if (challenge != null) {
                        val encodedChallenge = viewModel.encodeChallenge(challenge)
                        navController.goToWithPopCurrent(
                            Account.AuthenticationCompleted.buildRoute(
                                encodedChallenge = encodedChallenge, pin = pin
                            )
                        )
                    }
                },
                goToOneTimePassword = { challenge, pin ->
                    if (challenge != null) {
                        val encodedChallenge = viewModel.encodeChallenge(challenge)
                        navController.goToWithPopCurrent(
                            Account.OneTimePassword.buildRoute(
                                encodedChallenge = encodedChallenge,
                                pin = pin
                            )
                        )
                    }
                }
                ) { navController.popBackStack() }
        }
        composable(
            route = Account.AuthenticationCompleted.routeWithArgs,
            arguments = Account.AuthenticationCompleted.arguments,
        ) { _ ->
            AuthenticationCompletedScreen { navController.popBackStack() }
        }
        composable(
            route = Account.OneTimePassword.routeWithArgs,
            arguments = Account.OneTimePassword.arguments,
        ) { entry ->
            val viewModel = hiltViewModel<EduIdAuthenticationViewModel>(entry)
            OneTimePasswordScreen(viewModel = viewModel) { navController.popBackStack() }
        }
    }
}