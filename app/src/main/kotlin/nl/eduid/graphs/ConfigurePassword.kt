package nl.eduid.graphs

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import nl.eduid.graphs.ConfigurePassword.Form
import nl.eduid.graphs.ConfigurePassword.Request
import nl.eduid.screens.resetpassword.ResetPasswordScreen
import nl.eduid.screens.resetpassword.ResetPasswordViewModel
import nl.eduid.screens.resetpasswordconfirm.ResetPasswordConfirmScreen
import nl.eduid.screens.resetpasswordconfirm.ResetPasswordConfirmViewModel

fun NavGraphBuilder.configurePasswordFlow(
    navController: NavHostController,
    baseUrl: String,
    onConfigDone: () -> Unit,
) {
    navigation(
        startDestination = Request.route,
        route = Graph.CONFIGURE_PASSWORD,
    ) {
        composable(Request.route) {//region Reset password
            val viewModel = hiltViewModel<ResetPasswordViewModel>(it)
            ResetPasswordScreen(
                viewModel = viewModel,
                goToEmailSent = { email, reason ->
                    val currentRouteId = navController.currentDestination?.id ?: 0
                    navController.navigate(
                        RequestEduIdLinkSent.routeWithEmail(email, reason)
                    ) {
                        popUpTo(currentRouteId) { inclusive = true }
                    }
                },
            ) { navController.popBackStack() }
        }
        composable(
            Form.routeWithArgs, arguments = Form.arguments, deepLinks = listOf(navDeepLink {
                uriPattern = Form.getResetPassword(baseUrl)
            }, navDeepLink {
                uriPattern = Form.getAddPassword(baseUrl)
            })
        ) { entry ->
            val deepLinkIntent: Intent? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    entry.arguments?.getParcelable(
                        NavController.KEY_DEEP_LINK_INTENT,
                        Intent::class.java
                    )
                } else {
                    entry.arguments?.getParcelable(
                        NavController.KEY_DEEP_LINK_INTENT
                    )
                }
            val fullUri = deepLinkIntent?.data ?: Uri.EMPTY
            val isAddPassword = fullUri.path?.contains("add-password") ?: false
            val viewModel = hiltViewModel<ResetPasswordConfirmViewModel>(entry)
            ResetPasswordConfirmScreen(
                viewModel = viewModel,
                isAddPassword = isAddPassword,
                goBack = navController::popBackStack,
                onConfigDone = onConfigDone
            )
        }//endregion

    }
}

sealed class ConfigurePassword(val route: String) {
    data object Request : ConfigurePassword("request_hash_for_password_config")
    data object Form : ConfigurePassword("form_configure_password") {
        const val passwordHashArg = "h"
        val routeWithArgs = "${route}?$passwordHashArg={$passwordHashArg}"
        val arguments = listOf(navArgument(passwordHashArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        })

        fun getResetPassword(baseUrl: String) =
            "$baseUrl/client/mobile/reset-password?$passwordHashArg={$passwordHashArg}"

        fun getAddPassword(baseUrl: String) =
            "$baseUrl/client/mobile/add-password?$passwordHashArg={$passwordHashArg}"
    }
}
