package nl.eduid.screens.accountlinked

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.InfoField
import nl.eduid.ui.InfoTab
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGreen

@Composable
fun AccountLinkedScreen(
    viewModel: PersonalInfoViewModel,
    continueToHome: () -> Unit,
) {
    AccountLinkedContent(
        personalInfo = viewModel.uiState.personalInfo,
        isLoading = viewModel.uiState.isLoading,
        errorData = viewModel.uiState.errorData,
        dismissError = viewModel::clearErrorData,
        continueToHome = continueToHome,
        removeConnection = { index -> viewModel.removeConnection(index) },
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
) = EduIdTopAppBar(
    withBackIcon = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
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
        personalInfo.institutionAccounts.forEachIndexed { index, account ->
            InfoTab(
                header = if (index < 1) stringResource(R.string.infotab_role_institution) else "",
                title = account.role,
                subtitle = stringResource(R.string.infotab_at, account.roleProvider),
                institutionInfo = account,
                onClick = {},
                onDeleteButtonClicked = { removeConnection(index) },
                endIcon = R.drawable.chevron_down,
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