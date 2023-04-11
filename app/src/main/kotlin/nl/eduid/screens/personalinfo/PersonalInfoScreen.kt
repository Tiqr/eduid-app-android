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
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.InfoTab
import nl.eduid.ui.getDateTimeString
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.ButtonTextGrey
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun PersonalInfoScreen(
    viewModel: PersonalInfoViewModel,
    onNameClicked: () -> Unit,
    onEmailClicked: () -> Unit,
    onRoleClicked: () -> Unit,
    onInstitutionClicked: () -> Unit,
    onManageAccountClicked: (dateString: String) -> Unit,
    goBack: () -> Unit,
) {
    val personalInfo by viewModel.personalInfo.observeAsState(PersonalInfo())
    PersonalInfoScreenContent(
        onNameClicked = onNameClicked,
        onEmailClicked = onEmailClicked,
        onRoleClicked = onRoleClicked,
        onInstitutionClicked = onInstitutionClicked,
        onManageAccountClicked = onManageAccountClicked,
        goBack = goBack,
        personalInfo = personalInfo,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreenContent(
    onNameClicked: () -> Unit,
    onEmailClicked: () -> Unit,
    onRoleClicked: () -> Unit,
    onInstitutionClicked: () -> Unit,
    onManageAccountClicked: (dateString: String) -> Unit,
    goBack: () -> Unit,
    personalInfo: PersonalInfo,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(top = 42.dp, start = 26.dp, end = 26.dp),
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.back_button_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .size(width = 46.dp, height = 46.dp)
                            .clickable {
                                goBack.invoke()
                            },
                        alignment = Alignment.Center
                    )
                },
                title = {
                    Image(
                        painter = painterResource(R.drawable.ic_top_logo),
                        contentDescription = "",
                        modifier = Modifier.size(width = 122.dp, height = 46.dp),
                        alignment = Alignment.Center
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .padding(paddingValues)
                .padding(start = 26.dp, end = 26.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(36.dp))
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start,
                    color = ButtonGreen
                ),
                text = stringResource(R.string.personal_info_title),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                text = stringResource(R.string.personal_info_subtitle),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start,
                    color = ButtonGreen,
                    fontSize = 20.sp
                ),
                text = stringResource(R.string.personal_info_info_header),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            if (personalInfo.name.isBlank()) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(80.dp)
                        .width(80.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                )
            } else {
                InfoTab(
                    header = "Name",
                    title = personalInfo.name,
                    subtitle = "Provided by ${personalInfo.nameProvider}",
                    onClick = { },
                    endIcon = R.drawable.shield_tick_blue
                )
                InfoTab(
                    header = "Email",
                    title = personalInfo.email,
                    subtitle = "Provided by ${personalInfo.emailProvider}",
                    onClick = onEmailClicked,
                    endIcon = R.drawable.edit_icon
                )

                personalInfo.institutionAccounts.forEachIndexed {index, it ->
                    InfoTab(
                        header = if (index < 1) "Role & institution" else "",
                        title =  it.role,
                        subtitle = "At ${it.roleProvider}",
                        institutionInfo = it,
                        onClick = { },
                        endIcon = R.drawable.chevron_down,
                    )
                }

                Spacer(Modifier.height(42.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .border(
                            width = 1.dp,
                            color = TextGrayScale
                        )
                        .sizeIn(minHeight = 48.dp)
                        .fillMaxWidth()
                        .clickable {
                            onManageAccountClicked(personalInfo.dateCreated.getDateTimeString("EEEE, dd MMMM yyyy 'at' HH:MM"))
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.cog_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp)
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center),
                        text = "Manage your account",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Start,
                            color = TextGrayScale,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }
            Spacer(Modifier.height(42.dp))
        }
    }
}


@Preview
@Composable
private fun PreviewPersonalInfoScreenContent() = EduidAppAndroidTheme {
    PersonalInfoScreenContent(
        onNameClicked = {},
        onEmailClicked = {},
        onRoleClicked = {},
        onInstitutionClicked = {},
        goBack = {},
        personalInfo = PersonalInfo.demoData(),
        onManageAccountClicked = {},
    )
}