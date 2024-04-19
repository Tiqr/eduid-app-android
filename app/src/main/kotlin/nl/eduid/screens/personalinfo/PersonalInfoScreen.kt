package nl.eduid.screens.personalinfo

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.screens.firsttimedialog.LinkAccountContract
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.ConnectionCard
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.InfoFieldOld
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.annotatedStringWithBoldParts
import nl.eduid.ui.getDateTimeString
import nl.eduid.ui.theme.AlertInfoBackground
import nl.eduid.ui.theme.ButtonTextGrey
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.LinkAccountCard
import nl.eduid.ui.theme.MainSurfGreen
import nl.eduid.ui.theme.SmallActionGray
import nl.eduid.ui.theme.TextBlack

@Composable
fun PersonalInfoScreen(
    viewModel: PersonalInfoViewModel,
    onEmailClicked: () -> Unit,
    onNameClicked: (SelfAssertedName, Boolean) -> Unit = { _, _ -> },
    onManageAccountClicked: (dateString: String) -> Unit,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack,
) {
    var isGettingLinkUrl by rememberSaveable { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(contract = LinkAccountContract(), onResult = { _ ->
            /**We don't have to explicitly handle the result intent. The deep linking will
             * automatically open the [AccountLinkedScreen()] and ensure the backstack is correct.*/
        })

    if (isGettingLinkUrl && viewModel.uiState.haveValidLinkIntent()) {
        LaunchedEffect(key1 = viewModel) {
            isGettingLinkUrl = false
            launcher.launch(viewModel.uiState.linkUrl)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
    ) {
        PersonalInfoScreenContent(
            personalInfo = viewModel.uiState.personalInfo,
            isLoading = viewModel.uiState.isLoading,
            errorData = viewModel.uiState.errorData,
            dismissError = viewModel::clearErrorData,
            onEmailClicked = onEmailClicked,
            onNameClicked = onNameClicked,
            removeConnection = { index -> viewModel.removeConnection(index) },
            onManageAccountClicked = onManageAccountClicked,
            addLinkToAccount = {
                isGettingLinkUrl = true
                viewModel.requestLinkUrl()
            },
        )
    }
}

@Composable
fun PersonalInfoScreenContent(
    personalInfo: PersonalInfo,
    isLoading: Boolean = false,
    errorData: ErrorData? = null,
    dismissError: () -> Unit = {},
    onNameClicked: (SelfAssertedName, Boolean) -> Unit = { _, _ ->
    },
    onEmailClicked: () -> Unit = {},
    removeConnection: (Int) -> Unit = {},
    onManageAccountClicked: (dateString: String) -> Unit = {},
    addLinkToAccount: () -> Unit = {},
) = Column(
    modifier = Modifier
        .verticalScroll(rememberScrollState())
        .navigationBarsPadding()
        .padding(bottom = 24.dp)
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

    Text(
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Start, color = MainSurfGreen
        ),
        text = stringResource(R.string.Profile_Title_COPY),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp)
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
        text = stringResource(R.string.Profile_Info_COPY),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp)
    )
    if (!personalInfo.isVerified) {
        NotVerifiedBanner(addLinkToAccount)
    } else {
        Spacer(Modifier.height(12.dp))
    }
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        YourIdentityWithBanner(personalInfo.isVerified)
        Spacer(Modifier.height(12.dp))
        InfoFieldOld(
            title = personalInfo.seflAssertedName.chosenName.orEmpty(),
            subtitle = stringResource(R.string.Profile_FirstName_COPY),
            onClick = { onNameClicked },
            endIcon = R.drawable.edit_icon
        )
        Spacer(Modifier.height(12.dp))
        if (personalInfo.nameProvider == null) {
            InfoFieldOld(
                title = personalInfo.seflAssertedName.familyName.orEmpty(),
                subtitle = stringResource(R.string.Profile_LastName_COPY),
                onClick = { onNameClicked },
                endIcon = R.drawable.edit_icon
            )
        } else {

        }

        InfoFieldOld(
            title = personalInfo.name, subtitle = if (personalInfo.nameProvider == null) {
                annotatedStringWithBoldParts(
                    stringResource(R.string.Profile_ProvidedByYou_COPY), "you"
                )
            } else {
                annotatedStringWithBoldParts(
                    stringResource(R.string.Profile_ProvidedBy_COPY) + " " + personalInfo.nameProvider,
                    personalInfo.nameProvider
                )
            }, onClick = { onNameClicked }, endIcon = if (personalInfo.nameProvider == null) {
                R.drawable.edit_icon
            } else {
                R.drawable.shield_tick_blue
            }
        )
        Spacer(Modifier.height(16.dp))
        InfoFieldOld(
            title = personalInfo.email,
            subtitle = annotatedStringWithBoldParts(
                stringResource(R.string.Profile_ProvidedByYou_COPY),
//            stringResource(R.string.Profile_You_COPY)
            ),
            onClick = onEmailClicked,
            endIcon = R.drawable.edit_icon,
            capitalizeTitle = false,
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
                subtitle = stringResource(R.string.Profile_InstitutionAt_COPY) + " " + account.roleProvider,
                institutionInfo = account,
                onRemoveConnection = { removeConnection(index) },
            )
        }

        Spacer(Modifier.height(12.dp))
        LinkAccountCard(
            title = R.string.Profile_AddRoleAndInstitution_COPY,
            subtitle = R.string.Profile_AddViaSurfconext_COPY,
            enabled = !isLoading,
            addLinkToAccount = addLinkToAccount
        )
        Spacer(Modifier.height(42.dp))
        OutlinedButton(
            onClick = { onManageAccountClicked(personalInfo.dateCreated.getDateTimeString("EEEE, dd MMMM yyyy 'at' HH:MM")) },
            shape = RoundedCornerShape(CornerSize(6.dp)),
            modifier = Modifier
                .sizeIn(minHeight = 48.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.cog_icon),
                alignment = CenterStart,
                contentDescription = "",
                modifier = Modifier.padding(start = 24.dp, end = 24.dp)
            )
            Text(
                text = stringResource(R.string.Profile_ManageYourAccount_COPY),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Start,
                    color = ButtonTextGrey,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Composable
private fun ColumnScope.YourIdentityWithBanner(isVerified: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge.copy(color = MainSurfGreen),
            text = stringResource(R.string.Profile_YourIdentity_COPY),
        )
        if (isVerified) {
            Row(
                modifier = Modifier
                    .background(MainSurfGreen, RoundedCornerShape(6.dp))
                    .padding(8.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "",
                    tint = Color.White
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    text = stringResource(R.string.Profile_Verified_COPY),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        } else {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(R.string.Profile_NotVerified_COPY),
                modifier = Modifier
                    .background(SmallActionGray, RoundedCornerShape(6.dp))
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun ColumnScope.NotVerifiedBanner(addLinkToAccount: () -> Unit = {}) {
    Spacer(Modifier.height(24.dp))
    Column(
        modifier = Modifier
            .background(AlertInfoBackground)
            .padding(vertical = 12.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.shield_tick_blue),
                contentDescription = "",
            )
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                text = stringResource(R.string.Profile_VerifyNow_Title_COPY),
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        SecondaryButton(
            text = stringResource(R.string.Profile_VerifyNow_Button_COPY),
            color = TextBlack,
            onClick = addLinkToAccount,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

    }
    Spacer(Modifier.height(24.dp))
}

@Preview(locale = "nl")
@Composable
private fun Preview_PersonalInfoScreenContentNL() = EduidAppAndroidTheme {
    PersonalInfoScreenContent(
        personalInfo = PersonalInfo.demoData(),
    )
}

@Preview(locale = "nl")
@Composable
private fun Preview_NotVerifiedBannerNL() = Column {
    NotVerifiedBanner()
}

@Preview(locale = "en")
@Composable
private fun Preview_NotVerifiedBannerEN() = Column {
    NotVerifiedBanner()
}

@Preview(locale = "en")
@Composable
private fun Preview_YourIdentityBannerEN(
) = Column {
    YourIdentityWithBanner(isVerified = false)
    Spacer(modifier = Modifier.height(16.dp))
    YourIdentityWithBanner(isVerified = true)
}

@Preview(locale = "nl")
@Composable
private fun Preview_YourIdentityBannerNL(
) = Column {
    YourIdentityWithBanner(isVerified = false)
    Spacer(modifier = Modifier.height(16.dp))
    YourIdentityWithBanner(isVerified = true)
}

