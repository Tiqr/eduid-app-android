package nl.eduid.screens.editname

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.screens.firsttimedialog.LinkAccountContract
import nl.eduid.screens.personalinfo.PersonalInfo
import nl.eduid.screens.personalinfo.PersonalInfoViewModel
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.InfoField
import nl.eduid.ui.InfoTab
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.LinkAccountCard

@Composable
fun EditNameScreen(
    viewModel: PersonalInfoViewModel,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack,
) {
    var isGettingLinkUrl by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(contract = LinkAccountContract(), onResult = {
        /**We don't have to explicitly handle the result intent. The deep linking will
         * automatically open the [AccountLinkedScreen()] and ensure the backstack is correct.*/
    })

    if (isGettingLinkUrl && viewModel.uiState.haveValidLinkIntent()) {
        LaunchedEffect(key1 = viewModel) {
            isGettingLinkUrl = false
            launcher.launch(viewModel.uiState.linkUrl)
        }
    }

    EditNameContent(
        isLoading = viewModel.uiState.isLoading,
        personalInfo = viewModel.uiState.personalInfo,
        account = viewModel.uiState.personalInfo.institutionAccounts.firstOrNull(),
        updateName = { givenName, familyName -> viewModel.updateName(givenName, familyName) },
        addLinkToAccount = {
            isGettingLinkUrl = true
            viewModel.requestLinkUrl()
        },
        removeConnection = { index -> viewModel.removeConnection(index) },
    )
}

@Composable
private fun EditNameContent(
    isLoading: Boolean,
    personalInfo: PersonalInfo,
    account: PersonalInfo.InstitutionAccount? = null,
    updateName: (String, String) -> Unit = { _, _ -> },
    addLinkToAccount: () -> Unit = {},
    removeConnection: (Int) -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
) {
    Text(
        style = MaterialTheme.typography.titleLarge,
        text = stringResource(R.string.edit_name_title),
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        style = MaterialTheme.typography.titleLarge.copy(
            color = ButtonGreen
        ), text = stringResource(R.string.edit_name_subtitle), modifier = Modifier.fillMaxWidth()
    )
    if (isLoading) {
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
    Spacer(modifier = Modifier.height(24.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.ic_unverified_badge),
            contentDescription = null,
        )
        Spacer(
            modifier = Modifier.width(8.dp)
        )
        Text(
            text = stringResource(R.string.edit_name_selfasserted),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
    InfoField(
        title = personalInfo.name,
        subtitle = stringResource(R.string.infotab_providedby_you),
        endIcon = R.drawable.edit_icon
    )

    Spacer(
        modifier = Modifier.height(24.dp)
    )

    if (personalInfo.nameProvider != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_verified_badge), contentDescription = null
            )
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            Text(
                text = stringResource(R.string.edit_name_verified),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }

        InfoTab(
            title = personalInfo.name,
            subtitle = stringResource(
                R.string.infotab_providedby, personalInfo.nameProvider
            ),
            institutionInfo = account,
            onClick = {},
            onDeleteButtonClicked = { removeConnection(0) },
            endIcon = R.drawable.chevron_down,
        )

    }

    LinkAccountCard(
        title = R.string.edit_name_add_link_not_available,
        subtitle = R.string.edit_name_add_link_via,
        enabled = !isLoading,
        addLinkToAccount = addLinkToAccount
    )

    Spacer(
        modifier = Modifier.height(24.dp)
    )
}


@Preview
@Composable
private fun Preview_LinkAccountCard() {
    EduidAppAndroidTheme {
        EditNameContent(
            isLoading = false, personalInfo = PersonalInfo.demoData()
        )
    }
}