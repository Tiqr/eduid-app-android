package nl.eduid.screens.personalinfo

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import nl.eduid.R
import nl.eduid.screens.homepage.HomePageViewModel
import nl.eduid.ui.HomeGreenButton
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.SplashScreenBackgroundColor
import nl.eduid.ui.theme.TextBlack

@Composable
fun PersonalInfoScreen(
    viewModel: PersonalInfoViewModel,
    onNameClicked: () -> Unit,
    onEmailClicked: () -> Unit,
    onRoleClicked: () -> Unit,
    onInstitutionClicked: () -> Unit,
    goBack: () -> Unit,
) =
    PersonalInfoScreenContent(
        onNameClicked = onNameClicked,
        onEmailClicked = onEmailClicked,
        onRoleClicked = onRoleClicked,
        onInstitutionClicked = onInstitutionClicked,
        goBack = goBack,
        personalInfo = viewModel.personalInfo,
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreenContent(
    onNameClicked: () -> Unit,
    onEmailClicked: () -> Unit,
    onRoleClicked: () -> Unit,
    onInstitutionClicked: () -> Unit,
    goBack: () -> Unit,
    personalInfo: MutableLiveData<PersonalInfoViewModel.PersonalInfo>,
) = Scaffold(
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
        modifier = Modifier.padding(paddingValues)
    ) {
        Spacer(Modifier.height(36.dp))
        Text(
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center, color = ButtonGreen),
            text = stringResource(R.string.personal_info_title),
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text(
            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
            text = stringResource(R.string.personal_info_subtitle),
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text(
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center, color = ButtonGreen, fontSize = 20.sp),
            text = stringResource(R.string.personal_info_info_header),
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
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
        personalInfo = MutableLiveData()
    )
}