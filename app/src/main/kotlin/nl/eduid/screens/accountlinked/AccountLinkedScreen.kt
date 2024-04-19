package nl.eduid.screens.accountlinked

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
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
import nl.eduid.ui.VerifiedInfoField
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.MainSurfGreen

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
        when (result) {
            is ResultAccountLinked.FailedAlreadyLinkedResult -> AccountFailedLinkContent(
                explanation = stringResource(
                    R.string.NameUpdated_Title_FailReason_AlreadyLinked_COPY, result.withEmail
                ), continueToHome = continueToHome
            )

            ResultAccountLinked.FailedExpired -> AccountFailedLinkContent(
                explanation = stringResource(R.string.NameUpdated_Title_FailReason_SessionExpired_COPY),
                continueToHome = continueToHome
            )

            ResultAccountLinked.OK -> AccountLinkedContent(
                personalInfo = viewModel.uiState.personalInfo,
                isLoading = viewModel.uiState.isLoading,
                errorData = viewModel.uiState.errorData,
                dismissError = viewModel::clearErrorData,
                continueToHome = continueToHome,
                removeConnection = { index -> viewModel.removeConnection(index) },
            )
        }
    }
}

@Composable
private fun AccountFailedLinkContent(
    explanation: String,
    continueToHome: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(36.dp))
        Text(
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start, color = MainSurfGreen
            ),
            text = stringResource(R.string.NameUpdated_Title_YourSchool_COPY),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text(
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            text = stringResource(R.string.NameUpdated_Title_ContactedError_COPY),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = explanation,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
    PrimaryButton(
        text = stringResource(R.string.NameUpdated_Continue_COPY),
        onClick = continueToHome,
        modifier = Modifier.fillMaxWidth(),
    )

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AccountLinkedContent(
    personalInfo: PersonalInfo,
    isLoading: Boolean = false,
    errorData: ErrorData? = null,
    dismissError: () -> Unit = {},
    continueToHome: () -> Unit = {},
    removeConnection: (Int) -> Unit = {},
) = FlowColumn(
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
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = dismissError
        )
    }
    Spacer(Modifier.height(36.dp))
    Text(
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Start, color = MainSurfGreen
        ),
        text = stringResource(R.string.NameUpdated_Title_YourSchool_COPY),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        text = stringResource(R.string.NameUpdated_Title_ContactedSuccessfully_COPY),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.bodyLarge,
        text = stringResource(R.string.NameUpdated_Description_COPY),
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
    personalInfo.confirmedName.givenName?.let {
        VerifiedInfoField(
            title = it,
            subtitle = stringResource(id = R.string.Profile_VerifiedGivenName_COPY),
        )
    } ?: InfoField(
        title = personalInfo.seflAssertedName.chosenName.orEmpty(),
        subtitle = stringResource(id = R.string.Profile_ProvidedByYou_COPY)
    )
    Spacer(Modifier.height(16.dp))
    personalInfo.confirmedName.familyName?.let {
        VerifiedInfoField(
            title = it,
            subtitle = stringResource(id = R.string.Profile_VerifiedFamilyName_COPY),
        )
    } ?: InfoField(
        title = personalInfo.seflAssertedName.familyName.orEmpty(),
        subtitle = stringResource(id = R.string.Profile_ProvidedByYou_COPY)
    )
    Spacer(Modifier.height(16.dp))
    if (personalInfo.institutionAccounts.isNotEmpty()) {
        Text(
            text = stringResource(R.string.Profile_RoleAndInstitution_COPY),
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
            subtitle = stringResource(R.string.Profile_InstitutionAt_COPY, account.roleProvider),
            institutionInfo = account,
            onRemoveConnection = { removeConnection(index) },
        )
    }
    PrimaryButton(
        text = stringResource(R.string.NameUpdated_Continue_COPY),
        onClick = continueToHome,
        modifier = Modifier.fillMaxWidth(),
    )
}


@Preview
@Composable
private fun Preview_AccountLinkedContent() = EduidAppAndroidTheme {
    AccountLinkedContent(
        personalInfo = PersonalInfo.demoData()
    )
}