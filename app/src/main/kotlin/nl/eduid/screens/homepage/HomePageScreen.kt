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
    onCreateEduIdAccount: () -> Unit,
) {
    val haveRegisteredAccount by viewModel.haveRegisteredAccounts.observeAsState()

    when (haveRegisteredAccount) {
        true -> {
            HomePageWithAccountContent(
                onActivityClicked = onActivityClicked,
                onPersonalInfoClicked = onPersonalInfoClicked,
                onSecurityClicked = onSecurityClicked,
                onScanForAuthorization = onScanForAuthorization
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