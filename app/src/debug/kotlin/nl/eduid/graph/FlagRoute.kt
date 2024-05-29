package nl.eduid.graph

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class FlagRoute(val route: String) {
    object Overview : FlagRoute("flags_overview")
    object TestTheme : FlagRoute("test_theme")
    object EditFeatureFlags : FlagRoute("edit_feature_flags") {
        const val isTestSettings = "is_for_test_settings"
        val routeWithArgs = "${route}/{$isTestSettings}"
        val routeForTestSettings = "${route}/true"
        val routeForFeatureFlags = "${route}/false"
        val arguments = listOf(navArgument(isTestSettings) {
            type = NavType.BoolType
            nullable = false
            defaultValue = true
        })
    }
}