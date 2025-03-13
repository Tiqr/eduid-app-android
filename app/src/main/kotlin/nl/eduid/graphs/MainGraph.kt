package nl.eduid.graphs

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import nl.eduid.di.model.ControlCode
import nl.eduid.graphs.RequestEduIdLinkSent.LOGIN_REASON
import nl.eduid.graphs.RequestEduIdLinkSent.reasonArg
import nl.eduid.screens.accountlinked.AccountLinkedScreen
import nl.eduid.screens.accountlinked.AccountLinkedViewModel
import nl.eduid.screens.accountlinked.ResultAccountLinked
import nl.eduid.screens.biometric.EnableBiometricScreen
import nl.eduid.screens.biometric.EnableBiometricViewModel
import nl.eduid.screens.contbrowser.ContinueInBrowserScreen
import nl.eduid.screens.created.RequestEduIdCreatedScreen
import nl.eduid.screens.dataactivity.DataAndActivityScreen
import nl.eduid.screens.dataactivity.DataAndActivityViewModel
import nl.eduid.screens.deeplinks.DeepLinkScreen
import nl.eduid.screens.deeplinks.DeepLinkViewModel
import nl.eduid.screens.deleteaccountfirstconfirm.DeleteAccountFirstConfirmScreen
import nl.eduid.screens.deleteaccountsecondconfirm.DeleteAccountSecondConfirmScreen
import nl.eduid.screens.deleteaccountsecondconfirm.DeleteAccountSecondConfirmViewModel
import nl.eduid.screens.editemail.EditEmailScreen
import nl.eduid.screens.editemail.EditEmailViewModel
import nl.eduid.screens.editname.EditNameFormScreen
import nl.eduid.screens.editname.EditNameFormViewModel
import nl.eduid.screens.externalaccountlinkederror.ExternalAccountLinkedErrorScreen
import nl.eduid.screens.firsttimedialog.FirstTimeDialogRoute
import nl.eduid.screens.firsttimedialog.LinkAccountViewModel
import nl.eduid.screens.homepage.HomePageScreen
import nl.eduid.screens.homepage.HomePageViewModel
import nl.eduid.screens.manageaccount.ManageAccountScreen
import nl.eduid.screens.manageaccount.ManageAccountViewModel
import nl.eduid.screens.oauth.OAuthScreen
import nl.eduid.screens.oauth.OAuthViewModel
import nl.eduid.screens.personalinfo.PersonalInfoRoute
import nl.eduid.screens.personalinfo.PersonalInfoViewModel
import nl.eduid.screens.personalinfo.verified.VerifiedPersonalInfoRoute
import nl.eduid.screens.personalinfo.verified.VerifiedPersonalInfoViewModel
import nl.eduid.screens.pinsetup.NextStep
import nl.eduid.screens.pinsetup.RegistrationPinSetupScreen
import nl.eduid.screens.pinsetup.RegistrationPinSetupViewModel
import nl.eduid.screens.recovery.confirmsms.ConfirmCodeScreen
import nl.eduid.screens.recovery.confirmsms.ConfirmCodeViewModel
import nl.eduid.screens.recovery.requestsms.PhoneRequestCodeScreen
import nl.eduid.screens.recovery.requestsms.PhoneRequestCodeViewModel
import nl.eduid.screens.requestiddetails.RequestEduIdFormScreen
import nl.eduid.screens.requestiddetails.RequestEduIdFormViewModel
import nl.eduid.screens.requestidlinksent.RequestEduIdEmailSentScreen
import nl.eduid.screens.requestidstart.RequestEduIdStartScreen
import nl.eduid.screens.scan.ScanScreen
import nl.eduid.screens.scan.StatelessScanViewModel
import nl.eduid.screens.security.SecurityRoute
import nl.eduid.screens.security.SecurityViewModel
import nl.eduid.screens.selectyourbank.SelectYourBankScreen
import nl.eduid.screens.selectyourbank.SelectYourBankViewModel
import nl.eduid.screens.start.WelcomeStartScreen
import nl.eduid.screens.start.WelcomeStartViewModel
import nl.eduid.screens.twofactorkey.TwoFactorKeyScreen
import nl.eduid.screens.twofactorkey.TwoFactorKeyViewModel
import nl.eduid.screens.twofactorkeydelete.TwoFactorKeyDeleteScreen
import nl.eduid.screens.twofactorkeydelete.TwoFactorKeyDeleteViewModel
import nl.eduid.screens.verifyidentity.VerifyIdentityScreen
import nl.eduid.screens.verifyidentity.VerifyIdentityViewModel
import nl.eduid.screens.verifywithid.code.VerifyWithIdCodeViewModel
import nl.eduid.screens.verifywithid.input.VerifyWithIdInputScreen
import nl.eduid.screens.verifywithid.input.VerifyWithIdInputViewModel
import nl.eduid.screens.verifywithid.intro.VerifyWithIdCodeScreen
import nl.eduid.screens.verifywithid.intro.VerifyWithIdIntroScreen
import org.tiqr.data.model.EnrollmentChallenge

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainGraph(
    navController: NavHostController,
    baseUrl: String,
) = NavHost(
    navController = navController,
    startDestination = Graph.HOME_PAGE,
) {
    composable(Graph.HOME_PAGE) {//region Home
        val viewModel = hiltViewModel<HomePageViewModel>(it)

        HomePageScreen(viewModel = viewModel,
            onScanForAuthorization = { navController.navigate(Account.ScanQR.routeForAuth) },
            onActivityClicked = { navController.navigate(Graph.DATA_AND_ACTIVITY) },
            onPersonalInfoClicked = { navController.navigate(Graph.PERSONAL_INFO) },
            onSecurityClicked = { navController.navigate(Security.Settings.route) },
            onEnrollWithQR = { navController.navigate(Account.ScanQR.routeForEnrol) },
            launchOAuth = { navController.navigate(Graph.OAUTH) },
            goToRegistrationPinSetup = { challenge ->
                val encodeChallenge = viewModel.encodeChallenge(challenge)
                navController.navigate(
                    "${Account.EnrollPinSetup.route}/$encodeChallenge"
                )
            },
            goToConfirmDeactivation = { phoneNumber ->
                navController.navigate(
                    PhoneNumberRecovery.ConfirmCode.routeWithPhoneNumber(phoneNumber, true)
                )
            }) {
            navController.navigate(
                Graph.REQUEST_EDU_ID_ACCOUNT
            )
        }
    }//endregion

    composable(//region Scan
        route = Account.ScanQR.routeWithArgs, arguments = Account.ScanQR.arguments
    ) { entry ->
        val viewModel = hiltViewModel<StatelessScanViewModel>(entry)
        val isEnrolment = entry.arguments?.getBoolean(Account.ScanQR.isEnrolment, false) ?: false
        ScanScreen(viewModel = viewModel, isEnrolment = isEnrolment, goBack = { navController.popBackStack() }, goToNext = { challenge ->
            val encodedChallenge = viewModel.encodeChallenge(challenge)
            if (challenge is EnrollmentChallenge) {
                navController.goToWithPopCurrent(
                    "${Account.EnrollPinSetup.route}/$encodedChallenge"
                )
            } else {
                navController.goToWithPopCurrent(
                    "${Account.RequestAuthentication.route}/$encodedChallenge"
                )
            }
        })
    }//endregion

    composable(
//region PinSetup
        route = Account.EnrollPinSetup.routeWithArgs,
        arguments = Account.EnrollPinSetup.arguments,
    ) { entry ->
        val viewModel = hiltViewModel<RegistrationPinSetupViewModel>(entry)
        RegistrationPinSetupScreen(viewModel = viewModel, closePinSetupFlow = { navController.popBackStack() }) { nextStep ->
            when (nextStep) {
                NextStep.RecoveryInBrowser -> {
                    navController.navigate(Graph.CONTINUE_RECOVERY_IN_BROWSER)
                }

                is NextStep.PromptBiometric -> {
                    navController.navigate(
                        WithChallenge.EnableBiometric.buildRouteForEnrolment(
                            encodedChallenge = viewModel.encodeChallenge(nextStep.challenge), pin = nextStep.pin
                        )
                    ) {
                        popUpTo(Graph.HOME_PAGE)
                    }
                }

                NextStep.Recovery -> navController.navigate(PhoneNumberRecovery.RequestCode.route) {
                    popUpTo(Graph.HOME_PAGE)
                }
            }
        }
    }//endregion

    //region Authentication
    authenticationFlow(navController)
    //endregion

    composable(//region Notification Deep linking
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
                    "${Account.RequestAuthentication.route}/$encodedChallenge"
                )
            }
        })
    }//endregion

    composable(//region EnableBiometric
        route = WithChallenge.EnableBiometric.routeWithArgs, arguments = WithChallenge.arguments
    ) { entry ->
        val viewModel = hiltViewModel<EnableBiometricViewModel>(entry)
        EnableBiometricScreen(viewModel = viewModel, goToNext = { shouldAskForRecovery ->
            if (shouldAskForRecovery) {
                navController.navigate(PhoneNumberRecovery.RequestCode.route) {
                    popUpTo(Graph.HOME_PAGE)
                }
            } else {
                //Continue recovery via web
                navController.navigate(Graph.CONTINUE_RECOVERY_IN_BROWSER)
            }
        }) { navController.popBackStack() }
    }
    //endregion

    //region OAuth-Conditional
    composable(
        route = OAuth.route,
    ) { entry ->
        val viewModel = hiltViewModel<OAuthViewModel>(entry)
        ExampleAnimation {
            OAuthScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
    }//endregion

    composable(Graph.REQUEST_EDU_ID_ACCOUNT) {//region CreateAccount
        RequestEduIdStartScreen(requestId = { navController.navigate(Graph.REQUEST_EDU_ID_FORM) },
            onBackClicked = { navController.popBackStack() })
    }
    composable(Graph.REQUEST_EDU_ID_FORM) {
        val viewModel = hiltViewModel<RequestEduIdFormViewModel>(it)
        RequestEduIdFormScreen(viewModel = viewModel,
            goToEmailLinkSent = { email -> navController.goToEmailSent(email) },
            onBackClicked = { navController.popBackStack() })
    }

    composable(//region MagicLink sent
        route = RequestEduIdLinkSent.routeWithArgs, arguments = RequestEduIdLinkSent.arguments
    ) { entry ->
        RequestEduIdEmailSentScreen(
            userEmail = RequestEduIdLinkSent.decodeEmailFromEntry(entry), reason = entry.arguments?.getString(reasonArg) ?: ""
        ) { navController.popBackStack() }
    }//endregion
    composable(//region Account-Created
        route = RequestEduIdCreated.route, deepLinks = listOf(navDeepLink {
            uriPattern = RequestEduIdCreated.getUriPatternHttps(baseUrl)
        }, navDeepLink {
            uriPattern = RequestEduIdCreated.uriProdPatternHttps
        }, navDeepLink {
            uriPattern = RequestEduIdCreated.customScheme
        })
    ) { entry ->
        val viewModel = hiltViewModel<HomePageViewModel>(entry)
        RequestEduIdCreatedScreen(
            justCreated = true,
            viewModel = viewModel,
            goToOAuth = { navController.navigate(Graph.OAUTH) },
            goToRegistrationPinSetup = { challenge ->
                navController.navigate(
                    "${Account.EnrollPinSetup.route}/${
                        viewModel.encodeChallenge(
                            challenge
                        )
                    }"
                ) {
                    //Clear the entire flow for creating a new eduid account
                    popUpTo(Graph.REQUEST_EDU_ID_ACCOUNT) {
                        inclusive = true
                    }
                }
            },
        )//endregion
    }//endregion

    composable(
//region VerifyPhone-Recovery
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
        route = PhoneNumberRecovery.ConfirmCode.routeWithArgs, arguments = PhoneNumberRecovery.ConfirmCode.arguments
    ) { entry ->
        val viewModel = hiltViewModel<ConfirmCodeViewModel>(entry)
        val isDeactivation = entry.arguments?.getBoolean(PhoneNumberRecovery.ConfirmCode.isDeactivationArg, false) ?: false
        ConfirmCodeScreen(viewModel = viewModel, phoneNumber = PhoneNumberRecovery.ConfirmCode.decodeFromEntry(entry), goToStartScreen = {
            if (isDeactivation) {
                navController.popBackStack()
            } else {
                navController.navigate(Graph.WELCOME_START) {
                    //Flow for phone number recovery completed, remove from stack entirely
                    popUpTo(PhoneNumberRecovery.RequestCode.route) { inclusive = true }
                }
            }
        }) { navController.popBackStack() }
    }
    //endregion

    //region Welcome-FirstTime
    composable(Graph.WELCOME_START) { entry ->
        val viewModel = hiltViewModel<WelcomeStartViewModel>(entry)
        WelcomeStartScreen(
            viewModel,
        ) { accountIsAlreadyLinked ->
            if (accountIsAlreadyLinked) {
                navController.navigate(Graph.HOME_PAGE) {
                    //Clear existing home page that has no account
                    popUpTo(Graph.HOME_PAGE) {
                        inclusive = true
                    }
                }
            } else {
                navController.goToWithPopCurrent(Graph.FIRST_TIME_DIALOG)
            }
        }
    }
    composable(Graph.FIRST_TIME_DIALOG) { entry ->
        val viewModel = hiltViewModel<LinkAccountViewModel>(entry)
        FirstTimeDialogRoute(viewModel = viewModel,
            skipThis = {
                navController.navigate(Graph.HOME_PAGE) {
                    //Clear existing home page that has no account
                    popUpTo(Graph.HOME_PAGE) {
                        inclusive = true
                    }
                }
            })
    }
    composable(Graph.CONTINUE_RECOVERY_IN_BROWSER) {
        ContinueInBrowserScreen(goHome = {
            //Clear the entire backstack and reload the home page now that there is an account
            navController.navigate(Graph.HOME_PAGE) {
                popUpTo(Graph.HOME_PAGE) {
                    inclusive = true
                }
            }
        })
    }
    composable(//region Account Linked
        route = AccountLinked.routeWithArgs,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = AccountLinked.getUriPatternInternalLinkOK(baseUrl)
            },
            navDeepLink {
                uriPattern = AccountLinked.getUriPatternExternalLinkOK(baseUrl)
            },
            navDeepLink {
                uriPattern = AccountLinked.getUriPatternExternalLinkOKCustomScheme()
            },
            navDeepLink {
                uriPattern = AccountLinked.getUriPatternFailed(baseUrl)
            },
            navDeepLink {
                uriPattern = AccountLinked.getUriPatternSubjectAlreadyLinked(baseUrl)
            },
            navDeepLink {
                uriPattern = AccountLinked.getUriPatternSubjectAlreadyLinkedCustomScheme()
            },
            navDeepLink {
                uriPattern = AccountLinked.getUriPatternExpired(baseUrl)
            },
        ),
        arguments = AccountLinked.arguments
    ) { entry ->
        val deepLinkIntent: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            entry.arguments?.getParcelable(
                NavController.KEY_DEEP_LINK_INTENT, Intent::class.java
            )
        } else {
            @Suppress("DEPRECATION") entry.arguments?.getParcelable(
                NavController.KEY_DEEP_LINK_INTENT
            )
        }
        val fullUri = deepLinkIntent?.data ?: Uri.EMPTY
        val result = ResultAccountLinked.fromRedirectUrl(fullUri)

        val viewModel = hiltViewModel<AccountLinkedViewModel>(entry)
        AccountLinkedScreen(
            viewModel = viewModel,
            result = result,
            continueToHome = {
                navController.goToWithPopCurrent(Graph.HOME_PAGE)
            },
            continueToPersonalInfo = {
                navController.goToWithPopCurrent(Graph.HOME_PAGE) //Clear the entire backstack
                navController.navigate(Graph.PERSONAL_INFO)
            }
        )
    }//endregion
    //endregion

    composable(Graph.PERSONAL_INFO) {//region Home - Your Info
        val viewModel = hiltViewModel<PersonalInfoViewModel>(it)
        PersonalInfoRoute(
            viewModel = viewModel,
            onEmailClicked = { navController.navigate(Graph.EDIT_EMAIL) },
            onNameClicked = { name, canEditFamilyName ->
                navController.navigate(
                    EditName.Form.routeWithArgs(
                        name, canEditFamilyName
                    )
                )
            },
            onManageAccountClicked = { dateString ->
                navController.navigate(
                    ManageAccountRoute.routeWithArgs(
                        dateString
                    )
                )
            },
            openVerifiedInformation = {
                navController.navigate(VerifiedPersonalInfoRoute.route)
            },
            goToVerifyIdentity = { isLinkedAccount ->
                navController.navigate(VerifyIdentityRoute.routeWithArgs(isLinkedAccount))
            },
            goToCode = { code ->
                navController.navigate(VerifyIdentityWithIdCode.routeWithArgs(code))
            },
            goBack = navController::popBackStack,
        )
    }
    composable(VerifiedPersonalInfoRoute.route) { entry ->
        val viewModel = hiltViewModel<VerifiedPersonalInfoViewModel>(entry)
        VerifiedPersonalInfoRoute(viewModel = viewModel) {
            navController.popBackStack()
        }
    }//endregion

    composable(Graph.EDIT_EMAIL) {//region Edit email
        val viewModel = hiltViewModel<EditEmailViewModel>(it)
        EditEmailScreen(
            viewModel = viewModel,
            goBack = { navController.popBackStack() },
            onSaveNewEmailRequested = { email -> navController.goToEmailSent(email) },
        )
    }//endregion

    composable(route = EditName.Form.routeWithArgs, arguments = EditName.Form.arguments) {
        val viewModel = hiltViewModel<EditNameFormViewModel>(it)
        EditNameFormScreen(
            viewModel = viewModel,
            onNameChangeDone = {
                navController.navigate(Graph.PERSONAL_INFO) {
                    popUpTo(Graph.PERSONAL_INFO) {
                        inclusive = true
                    }
                }
            },
            goBack = { navController.popBackStack() },
        )
    }//endregion

    composable(//region Manage Account
        route = ManageAccountRoute.routeWithArgs, arguments = ManageAccountRoute.arguments
    ) { entry ->
        val viewModel = hiltViewModel<ManageAccountViewModel>(entry)
        ManageAccountScreen(
            viewModel = viewModel,
            goBack = { navController.popBackStack() },
        ) { navController.navigate(Graph.DELETE_ACCOUNT_FIRST_CONFIRM) }
    }

    composable(Graph.DELETE_ACCOUNT_FIRST_CONFIRM) {
        DeleteAccountFirstConfirmScreen(
            goBack = { navController.popBackStack() },
            onDeleteAccountPressed = { navController.navigate(Graph.DELETE_ACCOUNT_SECOND_CONFIRM) },
        )
    }

    composable(Graph.DELETE_ACCOUNT_SECOND_CONFIRM) {
        val viewModel = hiltViewModel<DeleteAccountSecondConfirmViewModel>(it)
        DeleteAccountSecondConfirmScreen(
            viewModel = viewModel,
            onAccountDeleted = {
                navController.navigate(Graph.HOME_PAGE) {
                    popUpTo(Graph.HOME_PAGE) {
                        inclusive = true
                    }
                }
            },
            goBack = { navController.popBackStack() },
        )
    }//endregion
    //endregion

    composable(Graph.DATA_AND_ACTIVITY) {//region Home - Activity
        val viewModel = hiltViewModel<DataAndActivityViewModel>(it)
        DataAndActivityScreen(
            viewModel = viewModel,
        ) { navController.popBackStack() }
    }//endregion

    composable(Security.Settings.route, deepLinks = listOf(navDeepLink {
        uriPattern = Security.ConfirmEmail.getConfirmEmail(baseUrl)
        action = Intent.ACTION_VIEW
    })) {//region Home - Security
        val viewModel = hiltViewModel<SecurityViewModel>(it)
        SecurityRoute(
            viewModel = viewModel,
            goBack = { navController.popBackStack() },
            onConfigurePasswordClick = { navController.navigate(Graph.CONFIGURE_PASSWORD) },
            onEditEmailClicked = { navController.navigate(Graph.EDIT_EMAIL) },
            on2FaClicked = { navController.navigate(Graph.TWO_FA_DETAIL) },
        )
    }
    composable(Graph.TWO_FA_DETAIL) {
        val viewModel = hiltViewModel<TwoFactorKeyViewModel>(it)
        TwoFactorKeyScreen(
            viewModel = viewModel,
            goBack = { navController.popBackStack() },
            onDeleteKeyPressed = { id ->
                navController.navigate(DeleteTwoFaRoute.routeWithArgs(id))
            },
        )
    }
    composable(
        route = DeleteTwoFaRoute.routeWithArgs, arguments = DeleteTwoFaRoute.arguments
    ) { entry ->
        val viewModel = hiltViewModel<TwoFactorKeyDeleteViewModel>(entry)
        TwoFactorKeyDeleteScreen(viewModel = viewModel,
            twoFaKeyId = DeleteTwoFaRoute.decodeIdFromEntry(entry),
            goBack = { navController.popBackStack() },
            onDeleteDone = {
                navController.navigate(Graph.TWO_FA_DETAIL) {
                    popUpTo(Graph.TWO_FA_DETAIL) { inclusive = true }
                }
            })
    }
    //region Configure Password: add, change or remove
    configurePasswordFlow(navController, baseUrl) { navController.navigate(Security.Settings.route) }
    //endregion

    //region Verify identity
    composable(VerifyIdentityRoute.routeWithArgs, arguments = VerifyIdentityRoute.arguments) {
        val viewModel = hiltViewModel<VerifyIdentityViewModel>(it)
        VerifyIdentityScreen(
            viewModel = viewModel,
            goToBankSelectionScreen = { navController.navigate(SelectYourBankRoute.route) },
            goToFallbackMethodScreen = { navController.navigate(VerifyIdentityWithIdIntro.route) },
            goBack = { navController.popBackStack() }
        )
    }

    composable(SelectYourBankRoute.route) {
        val viewModel = hiltViewModel<SelectYourBankViewModel>(it)
        SelectYourBankScreen(
            viewModel = viewModel,
            goBack = { navController.popBackStack() }
        )
    }

    composable(VerifyIdentityWithIdIntro.route) {
        VerifyWithIdIntroScreen(
            goBack = { navController.popBackStack() },
            goToEnterDetails = { navController.navigate(VerifyIdentityWithIdInput.route) }
        )
    }

    composable(
        route = VerifyIdentityWithIdInput.routeWithArgs,
        arguments = VerifyIdentityWithIdInput.arguments
    ) {
        val viewModel = hiltViewModel<VerifyWithIdInputViewModel>(it)
        VerifyWithIdInputScreen(
            viewModel = viewModel,
            goBack = {
                navController.goToWithPopCurrent(VerifyIdentityWithIdIntro.route)
             },
            goToGeneratedCode = { code ->
                val route = VerifyIdentityWithIdCode.routeWithArgs(code)
                navController.navigate(route)
            }
        )
    }
    composable(
        route = VerifyIdentityWithIdCode.routeWithArgs,
        arguments = VerifyIdentityWithIdCode.arguments) {
        val viewModel = hiltViewModel<VerifyWithIdCodeViewModel>(it)
        VerifyWithIdCodeScreen(
            viewModel = viewModel,
            goToPersonalInfo = {
                navController.goToWithPopCurrent(Graph.HOME_PAGE) //Clear the entire backstack
                navController.navigate(Graph.PERSONAL_INFO)
            },
            editCode = { controlCode ->
                navController.navigate(VerifyIdentityWithIdInput.routeWithArgs(controlCode))
            }
        )
    }


    composable(ExternalAccountLinkedError.route, deepLinks = listOf(navDeepLink {
        uriPattern = ExternalAccountLinkedError.getUriPattern(baseUrl)
    })) {
        ExternalAccountLinkedErrorScreen(
            goBack = {
                // We don't have a back stack anymore since this screen is always opened from a deeplink,
                // so we recreate the stack manually
                navController.popBackStack()
                navController.navigate(Graph.HOME_PAGE)
                navController.navigate(Graph.PERSONAL_INFO)
                navController.navigate(VerifyIdentityRoute.routeWithArgs(false))
            }
        )
    }
//endregion
}

fun NavController.goToEmailSent(email: String, reason: String = LOGIN_REASON) = navigate(
    RequestEduIdLinkSent.routeWithEmail(email, reason)
)

fun NavController.goToWithPopCurrent(destination: String) {
    val currentRouteId = currentDestination?.id ?: 0
    navigate(destination) {
        popUpTo(currentRouteId) { inclusive = true }
    }
}
