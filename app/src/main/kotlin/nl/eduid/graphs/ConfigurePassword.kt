package nl.eduid.graphs

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import nl.eduid.graphs.ConfigurePassword.*
import nl.eduid.screens.resetpassword.ResetPasswordScreen
import nl.eduid.screens.resetpassword.ResetPasswordViewModel
import nl.eduid.screens.resetpasswordconfirm.ResetPasswordConfirmScreen
import nl.eduid.screens.resetpasswordconfirm.ResetPasswordConfirmViewModel

fun NavGraphBuilder.configurePasswordFlow(navController: NavHostController) {
    navigation(
        startDestination = Request.route,
        route = Graph.CONFIGURE_PASSWORD,
    ) {
        composable(Request.route) {//region Reset password
            val viewModel = hiltViewModel<ResetPasswordViewModel>(it)
            ResetPasswordScreen(
                viewModel = viewModel,
                goToEmailSent = { email, reason -> navController.goToEmailSent(email, reason) },
            ) { navController.popBackStack() }
        }
        composable(
            Form.routeWithArgs, arguments = Form.arguments, deepLinks = listOf(navDeepLink {
                uriPattern = Form.resetPassword
            }, navDeepLink {
                uriPattern = Form.addPassword
            }, navDeepLink {
                uriPattern = Form.customSchemeResetPassword
            })
        ) {
            val viewModel = hiltViewModel<ResetPasswordConfirmViewModel>(it)
            ResetPasswordConfirmScreen(
                viewModel = viewModel,
                goBack = { navController.popBackStack() },
            )
        }//endregion

    }
}

sealed class ConfigurePassword(val route: String) {
    object Request : ConfigurePassword("request_hash_for_password_config")
    object Form : ConfigurePassword("form_configure_password") {
        const val passwordHashArg = "h"
        val routeWithArgs = "${route}?$passwordHashArg={$passwordHashArg}"
        val arguments = listOf(navArgument(passwordHashArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        })
        const val resetPassword =
            "https://login.test2.eduid.nl/client/mobile/reset-password?$passwordHashArg={$passwordHashArg}"
        const val addPassword =
            "https://login.test2.eduid.nl/client/mobile/add-password?$passwordHashArg={$passwordHashArg}"
        const val customSchemeResetPassword =
            "eduid://client/mobile/reset-password?$passwordHashArg={$passwordHashArg}"
    }
}

