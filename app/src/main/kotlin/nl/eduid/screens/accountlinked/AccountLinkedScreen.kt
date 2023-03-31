package nl.eduid.screens.accountlinked

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.screens.personalinfo.PersonalInfo
import nl.eduid.screens.personalinfo.PersonalInfoViewModel
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.InfoTab
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGreen

@Composable
fun AccountLinkedScreen(
    viewModel: PersonalInfoViewModel,
    continueToHome: () -> Unit,
) {
    val personalInfo by viewModel.personalInfo.observeAsState(PersonalInfo())
    AccountLinkedContent(
        personalInfo = personalInfo,
        continueToHome = continueToHome,
    )
}

@Composable
private fun AccountLinkedContent(
    personalInfo: PersonalInfo,
    continueToHome: () -> Unit = {},
) = EduIdTopAppBar(
    withBackIcon = false
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(36.dp))
        Text(
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start, color = TextGreen
            ),
            text = stringResource(R.string.account_linked_title),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text(
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            text = stringResource(R.string.account_linked_subtitle),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(R.string.account_linked_description),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        InfoTab(
            header = "Name",
            title = personalInfo.name,
            subtitle = "Provided by ${personalInfo.nameProvider}",
            onClick = { },
            endIcon = R.drawable.shield_tick_blue
        )

        personalInfo.institutionAccounts.forEach {
            InfoTab(
                header = "Role & institution",
                title = it.role,
                subtitle = it.institution,
                onClick = { },
                endIcon = R.drawable.shield_tick_blue,
            )
        }
        PrimaryButton(
            text = stringResource(R.string.button_continue),
            onClick = continueToHome,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(40.dp))
    }
}


@Preview
@Composable
private fun Preview_AccountLinkedContent() = EduidAppAndroidTheme {
    AccountLinkedContent(
        personalInfo = PersonalInfo.demoData()
    )
}