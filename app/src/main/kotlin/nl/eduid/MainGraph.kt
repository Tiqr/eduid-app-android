package nl.eduid

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import nl.eduid.enroll.EnrollScreen
import nl.eduid.login.LoginScreen
import nl.eduid.login.LoginViewModel
import nl.eduid.ready.ReadyScreen
import nl.eduid.requestiddetails.RequestIdDetailsScreen
import nl.eduid.requestiddetails.RequestIdDetailsViewModel
import nl.eduid.requestidlinksent.RequestIdLinkSentScreen
import nl.eduid.requestidstart.RequestIdStartScreen
import nl.eduid.splash.SplashScreen
import nl.eduid.splash.SplashViewModel

@Composable
fun MainGraph(navController: NavHostController) = NavHost(
    navController = navController, route = Graph.MAIN, startDestination = Graph.SPLASH
) {
    composable(Graph.SPLASH) {
        val viewModel = hiltViewModel<SplashViewModel>(it)
        SplashScreen(viewModel = viewModel,
            onFirstTimeUser = {
                navController.navigate(Graph.ENROLL) {
                    popUpTo(Graph.SPLASH) {
                        inclusive = true
                    }
                }
            }
        ) {
            navController.navigate(Graph.READY) {
                popUpTo(Graph.SPLASH) {
                    inclusive = true
                }
            }
        }
    }
    composable(Graph.ENROLL) {
        EnrollScreen(
            onLogin = { navController.navigate(Graph.LOGIN) },
            onScan = { navController.navigate(Graph.SCAN) },
            onRequestEduId = { navController.navigate(Graph.REQUEST_EDU_ID_START) }
        )
    }
    composable(Graph.READY) {
        ReadyScreen()
    }
    composable(Graph.LOGIN) {
        val viewModel = hiltViewModel<LoginViewModel>(it)
        LoginScreen(
            viewModel = viewModel,
            onLoginDone = {},
            goBack = { navController.popBackStack() })
    }
    composable(Graph.SCAN) {

    }
    composable(Graph.REQUEST_EDU_ID_START) {
        RequestIdStartScreen(
            requestId = { navController.navigate(Graph.REQUEST_EDU_ID_DETAILS) },
            onBackClicked = { navController.popBackStack() }
        )
    }
    composable(Graph.REQUEST_EDU_ID_DETAILS) {
        val viewModel = hiltViewModel<RequestIdDetailsViewModel>(it)
        RequestIdDetailsScreen(
            viewModel = viewModel,
            requestId = { email -> navController.navigate(Graph.REQUEST_EDU_ID_LINK_SENT + "/" + email) },
            onBackClicked = { navController.popBackStack() }
        )
    }

    composable(Graph.REQUEST_EDU_ID_LINK_SENT + "/{userId}") { backStackEntry ->
        RequestIdLinkSentScreen(
            userEmail = backStackEntry.arguments?.getString("userId") ?: "your email address",
            requestId = { },
            onBackClicked = { navController.popBackStack() }
        )
    }
}

object Graph {
    const val MAIN = "main_graph"
    const val SPLASH = "splash"
    const val ENROLL = "enroll"
    const val READY = "ready"
    const val LOGIN = "login"
    const val SCAN = "scan"
    const val REQUEST_EDU_ID_START = "request_edu_id_start"
    const val REQUEST_EDU_ID_DETAILS = "request_edu_id_details"
    const val REQUEST_EDU_ID_LINK_SENT = "request_edu_id_link_sent"
}