package nl.eduid.screens.personalinfo

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.screens.firsttimedialog.LinkAccountContract
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.ConnectionCard
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.InfoField
import nl.eduid.ui.getDateTimeString
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.ButtonTextGrey
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.LinkAccountCard

@Composable
fun PersonalInfoScreen(
    viewModel: PersonalInfoViewModel,
    onEmailClicked: () -> Unit,
    onNameClicked: () -> Unit = {},
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
    onNameClicked: () -> Unit = {},
    onEmailClicked: () -> Unit = {},
    removeConnection: (Int) -> Unit = {},
    onManageAccountClicked: (dateString: String) -> Unit = {},
    addLinkToAccount: () -> Unit = {},
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

    Text(
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Start, color = ButtonGreen
        ), text = stringResource(R.string.Profile_Title_COPY), modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
        text = stringResource(R.string.Profile_Info_COPY),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Start, color = ButtonGreen, fontSize = 20.sp
        ),
        text = stringResource(R.string.Profile_ShareableInformation_COPY),
        modifier = Modifier.fillMaxWidth()
    )
    if (isLoading) {
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
    Spacer(Modifier.height(12.dp))
    InfoField(
        title = personalInfo.name, subtitle = if (personalInfo.nameProvider == null) {
            stringResource(R.string.Profile_ProvidedByYou_COPY)
        } else {
            stringResource(R.string.Profile_ProvidedBy_COPY, personalInfo.nameProvider)
        }, onClick = onNameClicked, endIcon = if (personalInfo.nameProvider == null) {
            R.drawable.edit_icon
        } else {
            R.drawable.shield_tick_blue
        }, label = stringResource(R.string.Profile_Name_COPY)
    )
    Spacer(Modifier.height(16.dp))
    InfoField(
        title = personalInfo.email,
        subtitle = stringResource(R.string.Profile_ProvidedByYou_COPY),
        onClick = onEmailClicked,
        endIcon = R.drawable.edit_icon,
        capitalizeTitle = false,
        label = stringResource(R.string.Profile_Email_COPY),
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

    Spacer(Modifier.height(12.dp))
    LinkAccountCard(
        title = R.string.personalinfo_add_role_institution,
        subtitle = R.string.personalinfo_add_via,
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
            text = stringResource(R.string.personalinfo_manage_your_account),
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Start,
                color = ButtonTextGrey,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}


@Preview
@Composable
private fun PreviewPersonalInfoScreenContent() = EduidAppAndroidTheme {
    PersonalInfoScreenContent(
        personalInfo = PersonalInfo.demoData(),
    )
}