package nl.eduid.screens.homepage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier

@Composable
fun HomePageScreen(
    viewModel: HomePageViewModel,
    onScanForAuthorization: () -> Unit,
    onActivityClicked: () -> Unit,
    onPersonalInfoClicked: () -> Unit,
    onSecurityClicked: () -> Unit,
    onEnrollWithQR: () -> Unit,
    launchOAuth: () -> Unit,
    onCreateEduIdAccount: () -> Unit,
) {
    val haveRegisteredAccount by viewModel.haveRegisteredAccounts.observeAsState()
    val isAuthorizedForDataAccess by viewModel.isAuthorizedForDataAccess.observeAsState(false)
    val promptForAuth by viewModel.promptForAuth.observeAsState()

    when (haveRegisteredAccount) {
        true -> {
            HomePageWithAccountContent(
                isAuthorizedForDataAccess = isAuthorizedForDataAccess,
                shouldPromptAuthorization = promptForAuth,
                onActivityClicked = onActivityClicked,
                onPersonalInfoClicked = onPersonalInfoClicked,
                onSecurityClicked = onSecurityClicked,
                onScanForAuthorization = onScanForAuthorization,
                launchOAuth = launchOAuth,
                promptAuthorization = viewModel::promptForAuth,
                clearAuth = viewModel::clearPromptForAuth
            )
        }
        false -> {
            HomePageNoAccountContent(
                onScan = onEnrollWithQR,
                onRequestEduId = onCreateEduIdAccount
            )
        }
        else -> {
            Column(
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding(), verticalArrangement = Arrangement.Center
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )

            }

        }
    }
}