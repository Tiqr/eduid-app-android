package nl.eduid.screens.accountlinked

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.screens.personalinfo.PersonalInfo
import nl.eduid.screens.personalinfo.PersonalInfoViewModel
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.ConnectionCard
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.InfoField
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGreen

@Composable
fun AccountLinkedScreen(
    viewModel: PersonalInfoViewModel,
    result: ResultAccountLinked,
    continueToHome: () -> Unit,
) = EduIdTopAppBar(
    withBackIcon = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
    ) {
        if (result is ResultAccountLinked.OK) {
            AccountLinkedContent(
                personalInfo = viewModel.uiState.personalInfo,
                isLoading = viewModel.uiState.isLoading,
                errorData = viewModel.uiState.errorData,
                dismissError = viewModel::clearErrorData,
                continueToHome = continueToHome,
                removeConnection = { index -> viewModel.removeConnection(index) },
            )
        } else {
            AccountFailedLinkContent(result = result, continueToHome = continueToHome)
        }
    }
}

@Preview
@Composable
private fun AccountFailedLinkContent(
    result: ResultAccountLinked = ResultAccountLinked.FailedAlreadyLinkedResult,
    continueToHome: () -> Unit = {}
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween
) {
    Column(
        horizontalAlignment = Alignment.Start, modifier = Modifier
            .verticalScroll(rememberScrollState())
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
            text = stringResource(R.string.account_linked_fail_subtitle),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        val explanation =
            if (result is ResultAccountLinked.FailedExpired) {
                stringResource(R.string.account_linked_fail1_description)
            } else {
                stringResource(R.string.account_linked_fail2_description)
            }
        Text(
            text = explanation,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
    PrimaryButton(
        text = stringResource(R.string.button_continue),
        onClick = continueToHome,
        modifier = Modifier
            .fillMaxWidth(),
    )

}

@Composable
private fun AccountLinkedContent(
    personalInfo: PersonalInfo,
    isLoading: Boolean = false,
    errorData: ErrorData? = null,
    dismissError: () -> Unit = {},
    continueToHome: () -> Unit = {},
    removeConnection: (Int) -> Unit = {},
) = Column(
    modifier = Modifier
        .verticalScroll(rememberScrollState())
        .navigationBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
) {
    if (errorData != null) {
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = dismissError
        )
    }
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
    if (isLoading) {
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
    } else {
        Spacer(Modifier.height(12.dp))
    }
    InfoField(
        title = personalInfo.name,
        subtitle = if (personalInfo.nameProvider == null) {
            stringResource(R.string.infotab_providedby_you)
        } else {
            stringResource(R.string.infotab_providedby, personalInfo.nameProvider)
        },
        endIcon = R.drawable.shield_tick_blue,
        label = stringResource(R.string.infotab_fullname)
    )
    Spacer(Modifier.height(16.dp))
    if (personalInfo.institutionAccounts.isNotEmpty()) {
        Text(
            text = stringResource(R.string.infotab_role_institution),
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        Spacer(Modifier.height(6.dp))
    }
    personalInfo.institutionAccounts.forEachIndexed { index, account ->
        ConnectionCard(
            title = account.role,
            subtitle = stringResource(R.string.infotab_at, account.roleProvider),
            institutionInfo = account,
            onRemoveConnection = { removeConnection(index) },
        )
    }
    PrimaryButton(
        text = stringResource(R.string.button_continue),
        onClick = continueToHome,
        modifier = Modifier
            .fillMaxWidth(),
    )
}


@Preview
@Composable
private fun Preview_AccountLinkedContent() = EduidAppAndroidTheme {
    AccountLinkedContent(
        personalInfo = PersonalInfo.demoData()
    )
}