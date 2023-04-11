package nl.eduid.screens.personalinfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.InfoTab
import nl.eduid.ui.getDateTimeString
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.ButtonTextGrey
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun PersonalInfoScreen(
    viewModel: PersonalInfoViewModel,
    onEmailClicked: () -> Unit,
    onManageAccountClicked: (dateString: String) -> Unit,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack,
) {
    val uiState by viewModel.uiState.observeAsState(UiState())
    PersonalInfoScreenContent(
        personalInfo = uiState.personalInfo,
        isLoading = uiState.isLoading,
        errorData = uiState.errorData,
        dismissError = viewModel::clearErrorData,
        onEmailClicked = onEmailClicked,
        removeConnection = { index -> viewModel.removeConnection(index) },
        onManageAccountClicked = onManageAccountClicked,
    )
}

@Composable
fun PersonalInfoScreenContent(
    personalInfo: PersonalInfo,
    isLoading: Boolean = false,
    errorData: ErrorData? = null,
    dismissError: () -> Unit = {},
    onEmailClicked: () -> Unit = {},
    removeConnection: (Int) -> Unit = {},
    onManageAccountClicked: (dateString: String) -> Unit = {},
) = Column(
    verticalArrangement = Arrangement.Bottom,
    modifier = Modifier
        .verticalScroll(rememberScrollState())
) {
    if (errorData != null) {
        AlertDialogWithSingleButton(
            title = errorData.title,
            explanation = errorData.message,
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = dismissError
        )
    }

    Spacer(Modifier.height(36.dp))
    Text(
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Start, color = ButtonGreen
        ),
        text = stringResource(R.string.personal_info_title),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
        text = stringResource(R.string.personal_info_subtitle),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Start, color = ButtonGreen, fontSize = 20.sp
        ),
        text = stringResource(R.string.personal_info_info_header),
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
    InfoTab(
        header = stringResource(R.string.infotab_name),
        title = personalInfo.name,
        subtitle = if (personalInfo.nameProvider == null) {
            stringResource(
                R.string.infotab_providedby_you
            )
        } else {
            stringResource(
                R.string.infotab_providedby, personalInfo.nameProvider
            )
        },
        onClick = { },
        endIcon = if (personalInfo.nameProvider == null) {
            R.drawable.edit_icon
        } else {
            R.drawable.shield_tick_blue
        }
    )
    InfoTab(
        header = stringResource(R.string.infotab_email),
        title = personalInfo.email,
        subtitle = stringResource(R.string.infotab_providedby_you),
        onClick = onEmailClicked,
        endIcon = R.drawable.edit_icon
    )

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
            modifier = Modifier
                .padding(end = 48.dp)
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
    Spacer(Modifier.height(42.dp))
}


@Preview
@Composable
private fun PreviewPersonalInfoScreenContent() = EduidAppAndroidTheme {
    PersonalInfoScreenContent(
        personalInfo = PersonalInfo.demoData(),
    )
}