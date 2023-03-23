package nl.eduid.screens.personalinfo

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.eduid.R
import nl.eduid.ui.InfoTab
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun PersonalInfoScreen(
    viewModel: PersonalInfoViewModel,
    onNameClicked: () -> Unit,
    onEmailClicked: () -> Unit,
    onRoleClicked: () -> Unit,
    onInstitutionClicked: () -> Unit,
    goBack: () -> Unit,
) {
    val personalInfo by viewModel.personalInfo.observeAsState(PersonalInfo())
    PersonalInfoScreenContent(
        onNameClicked = onNameClicked,
        onEmailClicked = onEmailClicked,
        onRoleClicked = onRoleClicked,
        onInstitutionClicked = onInstitutionClicked,
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
                    onClick = { },
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
            }
            Spacer(Modifier.height(24.dp))
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
        personalInfo = PersonalInfo.demoData()
    )
}