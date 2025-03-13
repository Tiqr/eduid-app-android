package nl.eduid.screens.accountlinked

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.model.LinkedAccount
import nl.eduid.di.model.LinkedAccountUpdateRequest
import nl.eduid.screens.personalinfo.PersonalInfo
import nl.eduid.screens.personalinfo.PersonalInfoViewModel
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.ConnectionCard
import nl.eduid.ui.ConnectionCardOld
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.InfoField
import nl.eduid.ui.InfoFieldOld
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.VerifiedInfoField
import nl.eduid.ui.getShortDateString
import nl.eduid.ui.theme.ColorMain_Green_400
import nl.eduid.ui.theme.EduidAppAndroidTheme
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale

@Composable
fun AccountLinkedScreen(
    viewModel: AccountLinkedViewModel,
    result: ResultAccountLinked,
    continueToHome: () -> Unit,
    continueToPersonalInfo: () -> Unit
) = EduIdTopAppBar(
    withBackIcon = false
) {
    val accountLinked by viewModel.accountLinked.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = accountLinked) {
        if (accountLinked) {
            continueToPersonalInfo()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
    ) {

        when (result) {
            is ResultAccountLinked.FailedInstitutionAlreadyLinkedResult -> AccountFailedLinkContent(
                explanation = stringResource(
                    R.string.EppnAlreadyLinked_Info_COPY, result.withEmail
                ), continueToHome = continueToHome
            )

            is ResultAccountLinked.FailedExternalAccountAlreadyLinkedResult -> AccountFailedLinkContent(
                overrideTitle = stringResource(R.string.EppnAlreadyLinked_Title_VerificationFailed_COPY),
                explanation = if (result.withEmail == null) {
                    stringResource(R.string.EppnAlreadyLinked_InfoExternalAccountWithoutEmail_COPY)
                } else {
                    stringResource(R.string.EppnAlreadyLinked_InfoExternalAccountWithEmail_COPY, result.withEmail)
                }, continueToHome = continueToHome
            )

            ResultAccountLinked.FailedExpired -> AccountFailedLinkContent(
                explanation = stringResource(R.string.NameUpdated_Title_FailReason_SessionExpired_COPY),
                continueToHome = continueToHome
            )

            is ResultAccountLinked.Success -> {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val errorData by viewModel.errorData.collectAsStateWithLifecycle()
                val linkedAccount = viewModel.findLinkedAccount(uiState.personalInfo, result.institutionId)
                val isLinkingAccount by viewModel.isProcessing.collectAsStateWithLifecycle()
                AccountLinkedContent(
                    isLoading = uiState.isLoading,
                    errorData = errorData,
                    dismissError = viewModel::clearErrorData,
                    continueToHome = continueToHome,
                    linkedAccount = linkedAccount,
                    isRegistrationFlow = viewModel.isRegistrationFlow,
                    isFirstLinkedAccount = viewModel.isFirstLinkedAccount(uiState.personalInfo),
                    isLinkingAccount = isLinkingAccount,
                    preferLinkedAccount = {
                        if (linkedAccount != null) {
                            viewModel.preferLinkedAccount(linkedAccount)
                        }
                    },
                    continueToPersonalInfo = continueToPersonalInfo
                )
            }
        }
    }
}

@Composable
private fun AccountFailedLinkContent(
    explanation: String,
    overrideTitle: String? = null,
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
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(Modifier.height(36.dp))
        if (overrideTitle != null) {
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    color = ColorMain_Green_400
                ),
                text = overrideTitle,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start, color = ColorMain_Green_400
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
        }
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

@Composable
private fun AccountLinkedContent(
    linkedAccount: PersonalInfo.InstitutionAccount?,
    isRegistrationFlow: Boolean,
    isFirstLinkedAccount: Boolean,
    isLoading: Boolean = false,
    isLinkingAccount: Boolean = false,
    errorData: ErrorData? = null,
    dismissError: () -> Unit,
    preferLinkedAccount: () -> Unit,
    continueToPersonalInfo: () -> Unit,
    continueToHome: () -> Unit,
) = Column(
    modifier = Modifier
        .fillMaxSize()
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
            textAlign = TextAlign.Start, color = ColorMain_Green_400
        ),
        text = stringResource(R.string.LinkingSuccess_Title_COPY),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.bodyLarge,
        text = stringResource(R.string.LinkingSuccess_Subtitle_COPY),
        modifier = Modifier.fillMaxWidth()
    )
    if (isLoading) {
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
    } else {
        Spacer(Modifier.height(24.dp))
    }
    linkedAccount?.roleProvider?.let { roleProvider ->
        ConnectionCard(
            institutionName = roleProvider,
            role = (linkedAccount.role ?: linkedAccount.subjectId).replaceFirstChar { it.titlecase() },
            confirmedByInstitution = linkedAccount,
            isExpandable = false
        )
    }
    Spacer(Modifier.height(24.dp))
    if (!isFirstLinkedAccount) {
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(24.dp))
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(R.string.LinkingSuccess_SubtitlePreferInstitution_COPY),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
    }
    // Verified given name
    linkedAccount?.givenName?.let { givenName ->
        VerifiedInfoField(
             title = givenName,
             subtitle  = stringResource(R.string.Profile_VerifiedGivenName_COPY),
        )
        Spacer(Modifier.height(24.dp))
    }
    // Verified family name
    linkedAccount?.familyName?.let { familyName ->
        VerifiedInfoField(
            title = familyName,
            subtitle = stringResource(R.string.Profile_VerifiedFamilyName_COPY),
        )
        Spacer(Modifier.height(24.dp))
    }
    // Date of birth
    linkedAccount?.dateOfBirth?.let { dateOfBirth ->
        VerifiedInfoField(
            title = dateOfBirth.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli().getShortDateString(),
            subtitle = stringResource(R.string.Profile_VerifiedDateOfBirth_COPY),
        )
        Spacer(Modifier.height(24.dp))
    }
    Spacer(Modifier.weight(1f))
    if (isFirstLinkedAccount) {
        PrimaryButton(
            text = stringResource(R.string.LinkingSuccess_Button_Continue_COPY),
            onClick = if (isRegistrationFlow) continueToHome else continueToPersonalInfo,
            modifier = Modifier.fillMaxWidth(),
        )
    } else {
        PrimaryButton(
            text = stringResource(R.string.LinkingSuccess_Button_YesPlease_COPY),
            onClick = preferLinkedAccount,
            enabled = !isLinkingAccount,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.size(24.dp))
        PrimaryButton(
            text = stringResource(R.string.LinkingSuccess_Button_NoThanks_COPY),
            onClick = if (isRegistrationFlow) continueToHome else continueToPersonalInfo,
            buttonBackgroundColor = Color.Transparent,
            enabled = !isLinkingAccount,
            buttonTextColor = ColorMain_Green_400,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    Spacer(Modifier.height(16.dp))
}


@Preview
@Composable
private fun Preview_AccountLinkedContent() = EduidAppAndroidTheme {
    AccountLinkedContent(
        linkedAccount = PersonalInfo.InstitutionAccount(
            givenName = "Given name",
            familyName = "Family name",
            dateOfBirth = LocalDate.now(),
            subjectId = "1",
            role = "Librarian",
            roleProvider = "Library",
            institution = "University of Amsterdam",
            createdStamp = 0,
            expiryStamp = 0,
            updateRequest = LinkedAccountUpdateRequest(null, null, false, null)
        ),
        isFirstLinkedAccount = false,
        isLoading = false,
        isRegistrationFlow = false,
        errorData = null,
        dismissError = { },
        preferLinkedAccount = {},
        continueToHome = { },
        continueToPersonalInfo = { }
    )
}