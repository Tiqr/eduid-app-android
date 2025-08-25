package nl.eduid.graphs

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.LaunchedEffect
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
import nl.eduid.screens.emailcodeentry.EmailCodeEntryViewModel
import nl.eduid.screens.resetpassword.Password
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
            val savedStateHandle = it.savedStateHandle
            // Check if a result was returned from the one time email code verification
            LaunchedEffect(savedStateHandle) {
                savedStateHandle.get<String>(EmailCodeEntryViewModel.KEY_CODE_RESULT_HASH)?.let { hash ->
                    navController.popBackStack() // The user should not be able to return to this screen anymore
                    navController.navigate(
                        Form.routeWithArgs(viewModel.uiState.password,hash)
                    )
                }
            }
            ResetPasswordScreen(
                viewModel = viewModel,
                goToEmailSent = { email, reason ->
                    navController.navigate(
                        EmailCodeEntry.routeWithArgs(
                            email = email,
                            codeHash = null,
                            codeContext = if (viewModel.uiState.password == Password.Change) {
                                EmailCodeEntryViewModel.CodeContext.ChangePassword
                            } else {
                                EmailCodeEntryViewModel.CodeContext.AddPassword
                            },
                        )
                    )
                },
            ) { navController.popBackStack() }
        }
        composable(
            Form.routeWithArgs, arguments = Form.arguments, deepLinks = listOf(navDeepLink {
                uriPattern = Form.getResetPasswordDeeplink(baseUrl)
            }, navDeepLink {
                uriPattern = Form.getAddPasswordDeeplink(baseUrl)
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
            val typeArg = entry.savedStateHandle.get<String>(Form.typeArg)?.let { Password.valueOf(it) }
            val isAddPassword = if (typeArg == null) {
                fullUri.path?.contains("add-password") ?: false
            }  else {
                typeArg == Password.Add
            }
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
        const val typeArg = "type"

        val routeWithArgs = "${route}?$passwordHashArg={$passwordHashArg}&$typeArg={$typeArg}"
        val arguments = listOf(navArgument(passwordHashArg) {
            type = NavType.StringType
            nullable = false
            defaultValue = ""
        }, navArgument(typeArg) {
            type = NavType.StringType
            nullable = true
            defaultValue = Password.Change.name
        })

        fun getResetPasswordDeeplink(baseUrl: String) =
            "$baseUrl/client/mobile/reset-password?$passwordHashArg={$passwordHashArg}"

        fun getAddPasswordDeeplink(baseUrl: String) =
            "$baseUrl/client/mobile/add-password?$passwordHashArg={$passwordHashArg}"

        fun routeWithArgs(type: Password, passwordHash: String): String {
            return "$route?$passwordHashArg=$passwordHash&$typeArg=${type.name}"
        }
    }
}
