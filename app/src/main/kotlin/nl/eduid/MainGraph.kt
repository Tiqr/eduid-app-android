package nl.eduid

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import nl.eduid.enroll.EnrollScreen
import nl.eduid.login.LoginScreen
import nl.eduid.ready.ReadyScreen
import nl.eduid.splash.SplashScreen
import nl.eduid.splash.SplashViewModel

@Composable
fun MainGraph(navController: NavHostController) = NavHost(
    navController = navController, route = Graph.MAIN, startDestination = Graph.SPLASH
) {
    composable(Graph.SPLASH) {
        val viewModel = hiltViewModel<SplashViewModel>(it)
        SplashScreen(viewModel = viewModel,
            onFirstTimeUser = { navController.navigate(Graph.ENROLL) },
            onSignIn = { navController.navigate(Graph.LOGIN) },
            onResumeApp = { navController.navigate(Graph.READY) })
    }
    composable(Graph.ENROLL) {
        EnrollScreen()
    }
    composable(Graph.READY) {
        ReadyScreen()
    }
    composable(Graph.LOGIN) {
        LoginScreen()
    }
}

object Graph {
    const val MAIN = "main_graph"
    const val SPLASH = "splash"
    const val ENROLL = "enroll"
    const val READY = "ready"
    const val LOGIN = "login"
}