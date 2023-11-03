package nl.eduid.screens.editname

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.screens.firsttimedialog.LinkAccountContract
import nl.eduid.screens.personalinfo.PersonalInfo
import nl.eduid.screens.personalinfo.PersonalInfoViewModel
import nl.eduid.ui.ConnectionCard
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.InfoField
import nl.eduid.ui.TwoColorTitle
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.LinkAccountCard

@Composable
fun EditNameScreen(
    viewModel: PersonalInfoViewModel,
    updateName: (SelfAssertedName) -> Unit = { _ -> },
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
        padding = it,
        updateName = updateName,
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
    padding: PaddingValues = PaddingValues(),
    updateName: (SelfAssertedName) -> Unit = { _ -> },
    addLinkToAccount: () -> Unit = {},
    removeConnection: (Int) -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(padding)
        .navigationBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
) {
    TwoColorTitle(
        firstPart = stringResource(R.string.NameOverview_Title_AllDetailsOf_COPY),
        secondPart = stringResource(R.string.NameOverview_Title_FullName_COPY)
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
            text = stringResource(R.string.NameOverview_SelfAsserted_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
    InfoField(
        title = "${personalInfo.seflAssertedName.givenName} ${personalInfo.seflAssertedName.familyName}",
        subtitle = stringResource(R.string.Profile_ProvidedByYou_COPY),
        endIcon = R.drawable.edit_icon,
        onClick = { updateName(personalInfo.seflAssertedName) },
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
                text = stringResource(R.string.NameOverview_Verified_COPY),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
        ConnectionCard(
            title = personalInfo.name,
            subtitle = stringResource(R.string.Profile_ProvidedBy_COPY, personalInfo.nameProvider),
            institutionInfo = account,
            onRemoveConnection = { removeConnection(0) },
        )
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_verified_badge), contentDescription = null
            )
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            Text(
                text = stringResource(R.string.NameOverview_AnotherSource_COPY),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
        LinkAccountCard(
            title = R.string.NameOverview_NotAvailable_COPY,
            subtitle = R.string.NameOverview_ProceedToAdd_COPY,
            enabled = !isLoading,
            addLinkToAccount = addLinkToAccount
        )
    }
}


@Preview
@Composable
private fun Preview_EditNameContent() {
    EduidAppAndroidTheme {
        EditNameContent(
            isLoading = false, personalInfo = PersonalInfo.demoData()
        )
    }
}