package nl.eduid.screens.personalinfo

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.theapache64.rebugger.Rebugger
import kotlinx.coroutines.flow.filterNotNull
import nl.eduid.R
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.screens.firsttimedialog.LinkAccountContract
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.ConnectionCard
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.ExpandableVerifiedInfoField
import nl.eduid.ui.InfoField
import nl.eduid.ui.getDateTimeString
import nl.eduid.ui.theme.ColorSupport_Blue_100
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.LinkAccountCard

@Composable
fun PersonalInfoRoute(
    viewModel: PersonalInfoViewModel,
    onEmailClicked: () -> Unit,
    onNameClicked: (SelfAssertedName, Boolean) -> Unit,
    onManageAccountClicked: (dateString: String) -> Unit,
    openVerifiedInformation: (String) -> Unit,
    goBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorData by viewModel.errorData.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var isGettingLinkUrl by rememberSaveable { mutableStateOf(false) }
    val onEmail = remember(viewModel) { { onEmailClicked() } }
    val onBack = remember(viewModel) { { goBack() } }
    val onName = remember(viewModel) {
        { onNameClicked(uiState.personalInfo.seflAssertedName, !uiState.personalInfo.isVerified) }
    }
    val addLink = remember(viewModel) {
        {
            viewModel.requestLinkUrl()
            isGettingLinkUrl = true
        }
    }
    val createdAtDate = stringResource(R.string.ManageAccount_CreatedAt_COPY)
    val onManage = remember(viewModel) {
        { onManageAccountClicked(uiState.personalInfo.dateCreated.getDateTimeString(createdAtDate)) }
    }
    val isLoading by remember {
        derivedStateOf {
            uiState.isLoading || isProcessing
        }
    }
    val launcher =
        rememberLauncherForActivityResult(contract = LinkAccountContract(), onResult = { _ ->
            /**We don't have to explicitly handle the result intent. The deep linking will
             * automatically open the [AccountLinkedScreen()] and ensure the backstack is correct.*/
        })

    if (isGettingLinkUrl) {
        LaunchedEffect(viewModel, lifecycle) {
            viewModel.linkUrl.filterNotNull().flowWithLifecycle(lifecycle).collect {
                launcher.launch(it)
                isGettingLinkUrl = false
            }
        }
    }

    errorData?.let { error ->
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = error.title(context),
            explanation = error.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = viewModel::clearErrorData
        )
    }
    PersonalInfoScreen(
        uiState = uiState,
        isLoading = isLoading,
        modifier = Modifier,
        onNameClicked = onName,
        onEmailClicked = onEmail,
        onManageAccountClicked = onManage,
        openVerifiedInformation = openVerifiedInformation,
        addLinkToAccount = addLink,
        goBack = onBack,
    )
}

@Composable
fun PersonalInfoScreen(
    uiState: UiState,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onNameClicked: () -> Unit,
    onEmailClicked: () -> Unit,
    onManageAccountClicked: () -> Unit,
    openVerifiedInformation: (String) -> Unit,
    addLinkToAccount: () -> Unit,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack,
) {
    Rebugger(
        trackMap = mapOf(
            "uiState" to uiState,
            "isLoading" to isLoading,
            "modifier" to modifier,
            "onNameClicked" to onNameClicked,
            "onEmailClicked" to onEmailClicked,
            "onManageAccountClicked" to onManageAccountClicked,
            "openVerifiedInformation" to openVerifiedInformation,
            "addLinkToAccount" to addLinkToAccount,
            "goBack" to goBack
        )
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(it)
            .systemBarsPadding()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSecondary
            ),
            text = stringResource(R.string.Profile_Title_COPY),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(R.string.Profile_Info_COPY),
        )
        if (!isLoading) {
            if (!uiState.personalInfo.isVerified) {
                NotVerifiedIdentity(
                    selfAssertedName = uiState.personalInfo.seflAssertedName,
                    isLoading = isLoading,
                    addLinkToAccount = addLinkToAccount,
                    onNameClicked = onNameClicked
                )
            } else {
                VerifiedIdentity(
                    personalInfo = uiState.personalInfo,
                    isLoading = isLoading,
                    onNameClicked = onNameClicked,
                    openVerifiedInformation = openVerifiedInformation
                )
            }

            Text(
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSecondary),
                text = stringResource(R.string.Profile_ContactDetails_COPY),
                modifier = Modifier.padding(top = 16.dp)
            )
            InfoField(title = uiState.personalInfo.email,
                subtitle = stringResource(R.string.Profile_Email_COPY),
                modifier = Modifier.clickable {
                    onEmailClicked()
                })

            if (uiState.personalInfo.institutionAccounts.isNotEmpty()) {
                RoleAndInstitutions(openVerifiedInformation, uiState.personalInfo.institutionAccounts)
            }

            LinkAccountCard(
                title = R.string.Profile_AddRoleAndInstitution_COPY,
                subtitle = R.string.Profile_AddViaSurfconext_COPY,
                enabled = !isLoading,
                addLinkToAccount = addLinkToAccount
            )
            val configuration = LocalConfiguration.current
            Surface(
                modifier = Modifier
                    .requiredWidth(configuration.screenWidthDp.dp)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(vertical = 12.dp, horizontal = 24.dp),
            ) {
                OutlinedButton(
                    onClick = onManageAccountClicked,
                    shape = RoundedCornerShape(CornerSize(6.dp)),
                    modifier = Modifier
                        .sizeIn(minHeight = 48.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.cog_icon),
                        alignment = CenterStart,
                        contentDescription = "",
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Text(
                        text = stringResource(R.string.Profile_ManageYourAccount_COPY),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                    )
                }
            }
        } else {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ColumnScope.RoleAndInstitutions(
    openVerifiedInformation: (String) -> Unit,
    institutionAccounts: List<PersonalInfo.InstitutionAccount>,
) {
    Text(
        text = stringResource(R.string.Profile_RoleAndInstitution_COPY),
        style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSecondary),
        modifier = Modifier.padding(top = 16.dp)
    )
    institutionAccounts.forEach { account ->
        ConnectionCard(
            title = account.role,
            confirmedByInstitution = account,
            openVerifiedInformation = openVerifiedInformation
        )
    }
}

@Composable
private fun ColumnScope.NotVerifiedIdentity(
    selfAssertedName: SelfAssertedName,
    isLoading: Boolean,
    addLinkToAccount: () -> Unit,
    onNameClicked: () -> Unit,
) {
    NotVerifiedBanner(addLinkToAccount)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSecondary),
            text = stringResource(R.string.Profile_YourIdentity_COPY),
        )
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(R.string.Profile_NotVerified_COPY),
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(6.dp)
                )
                .padding(8.dp)
        )
    }

    if (isLoading) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    }
    InfoField(title = selfAssertedName.chosenName.orEmpty(),
        subtitle = stringResource(R.string.Profile_FirstName_COPY),
        modifier = Modifier.clickable {
            onNameClicked()
        })
    InfoField(title = selfAssertedName.familyName.orEmpty(),
        subtitle = stringResource(R.string.Profile_LastName_COPY),
        modifier = Modifier.clickable {
            onNameClicked()
        })
}

@Composable
private fun ColumnScope.VerifiedIdentity(
    personalInfo: PersonalInfo,
    isLoading: Boolean,
    onNameClicked: () -> Unit,
    openVerifiedInformation: (String) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSecondary),
            text = stringResource(R.string.Profile_YourIdentity_COPY),
        )
        Row(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(6.dp)
                )
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold
                ),
                text = stringResource(R.string.Profile_Verified_COPY),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
    InfoField(title = personalInfo.seflAssertedName.chosenName.orEmpty(),
        subtitle = stringResource(R.string.Profile_FirstName_COPY),
        modifier = Modifier.clickable {
            onNameClicked()
        })
    ExpandableVerifiedInfoField(
        title = personalInfo.confirmedName.givenName.orEmpty(),
        subtitle = stringResource(R.string.Profile_VerifiedGivenName_COPY),
        confirmedByInstitution = personalInfo.institutionAccounts.first {
            it.id == personalInfo.confirmedName.givenNameConfirmedBy.orEmpty()
        },
        openVerifiedInformation = openVerifiedInformation
    )
    ExpandableVerifiedInfoField(
        title = personalInfo.confirmedName.familyName.orEmpty(),
        subtitle = stringResource(R.string.Profile_VerifiedFamilyName_COPY),
        confirmedByInstitution = personalInfo.institutionAccounts.first {
            it.id == personalInfo.confirmedName.familyNameConfirmedBy.orEmpty()
        },
        openVerifiedInformation = openVerifiedInformation
    )
}

@Composable
private fun ColumnScope.NotVerifiedBanner(addLinkToAccount: () -> Unit = {}) {
    val configuration = LocalConfiguration.current
    Column(
        modifier = Modifier
            .requiredWidth(configuration.screenWidthDp.dp)
            .background(ColorSupport_Blue_100)
            .padding(vertical = 12.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.shield_tick_blue),
                contentDescription = "",
            )
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                text = stringResource(R.string.Profile_VerifyNow_Title_COPY),
            )
        }
        OutlinedButton(
            onClick = addLinkToAccount,
            shape = RoundedCornerShape(CornerSize(6.dp)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .sizeIn(minHeight = 48.dp),
        ) {
            Text(
                text = stringResource(R.string.Profile_VerifyNow_Button_COPY),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            )
        }
    }
}

@Preview(locale = "en", showBackground = true)
@Preview(locale = "nl", showBackground = true)
@Composable
private fun Preview_PersonalInfoScreen() = EduidAppAndroidTheme {
    PersonalInfoScreen(uiState = UiState(PersonalInfo.demoData()),
        isLoading = false,
        onNameClicked = { },
        onEmailClicked = {},
        onManageAccountClicked = {},
        openVerifiedInformation = { _ -> },
        addLinkToAccount = {},
        goBack = {})
}

@Preview(locale = "en", showBackground = true)
@Preview(locale = "nl", showBackground = true)
@Composable
private fun Preview_VerifiedIdentity() = EduidAppAndroidTheme {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        VerifiedIdentity(personalInfo = PersonalInfo.verifiedDemoData(),
            isLoading = false,
            onNameClicked = {})
    }
}

@Preview(locale = "en", showBackground = true)
@Preview(locale = "nl", showBackground = true)
@Composable
private fun Preview_RoleAndInstitutions() = EduidAppAndroidTheme {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        RoleAndInstitutions(institutionAccounts = PersonalInfo.generateInstitutionAccountList(),
            openVerifiedInformation = {})
    }
}