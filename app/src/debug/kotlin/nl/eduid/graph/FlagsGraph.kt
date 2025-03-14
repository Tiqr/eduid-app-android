package nl.eduid.graph

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import nl.eduid.ThemeSample
import nl.eduid.overview.OverviewScreen
import nl.eduid.settings.EditFeatureFlagsScreen
import nl.eduid.settings.EditFeatureFlagsViewModel

@Composable
fun FlagsGraph(
    navController: NavHostController,
) = NavHost(
    navController = navController, startDestination = FlagRoute.Overview.route
) {
    composable(FlagRoute.Overview.route) {//region Home
        OverviewScreen(gotoFeatureFlags = {
            navController.navigate(FlagRoute.EditFeatureFlags.routeForFeatureFlags)
        }, gotoTestSettings = {
            navController.navigate(FlagRoute.EditFeatureFlags.routeForTestSettings)
        }, goToTheme = { navController.navigate(FlagRoute.TestTheme.route) }
        )
    }
    composable(
        route = FlagRoute.EditFeatureFlags.routeWithArgs,
        arguments = FlagRoute.EditFeatureFlags.arguments
    ) {
        val viewModel = hiltViewModel<EditFeatureFlagsViewModel>(it)
        EditFeatureFlagsScreen(viewModel)
    }
    composable(
        route = FlagRoute.TestTheme.route,
    ) {
        ThemeSample()
    }
}