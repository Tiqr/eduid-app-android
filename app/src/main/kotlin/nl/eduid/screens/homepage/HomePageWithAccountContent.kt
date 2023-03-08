package nl.eduid.screens.homepage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.HomeGreenButton
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.SplashScreenBackgroundColor
import nl.eduid.ui.theme.TextBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageWithAccountContent(
    onActivityClicked: () -> Unit,
    onPersonalInfoClicked: () -> Unit,
    onSecurityClicked: () -> Unit,
    onScanForAuthorization: () -> Unit,
) = Scaffold(
    topBar = {
        CenterAlignedTopAppBar(
            modifier = Modifier.padding(top = 42.dp, start = 26.dp, end = 26.dp),
            navigationIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_top_scan),
                    contentDescription = stringResource(R.string.button_scan),
                    modifier = Modifier
                        .size(width = 32.dp, height = 32.dp)
                        .clickable {
                            onScanForAuthorization()
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
    ConstraintLayout(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        val (title, bottomColumn) = createRefs()

        Text(
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = ButtonGreen)) {
                    append(stringResource(R.string.homepage_title_one))
                }
                append("\n")
                withStyle(style = SpanStyle(color = TextBlack)) {
                    append(stringResource(R.string.homepage_title_two))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    bottom.linkTo(bottomColumn.top)
                },
        )

        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.constrainAs(bottomColumn) {
                bottom.linkTo(parent.bottom)
            },
        ) {
            Image(
                painter = painterResource(id = R.drawable.medidate_image),
                contentDescription = "",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .background(SplashScreenBackgroundColor)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HomeGreenButton(
                        text = "Security",
                        onClick = onSecurityClicked,
                        icon = R.drawable.homepage_security_icon
                    )
                    HomeGreenButton(
                        text = "Personal Info",
                        onClick = onPersonalInfoClicked,
                        icon = R.drawable.homepage_info_icon
                    )
                    HomeGreenButton(
                        text = "Activity",
                        onClick = onActivityClicked,
                        icon = R.drawable.homepage_activity_icon
                    )
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHomePageScreen() = EduidAppAndroidTheme {
    HomePageWithAccountContent(
        onScanForAuthorization = {},
        onActivityClicked = {},
        onPersonalInfoClicked = {},
        onSecurityClicked = {},
    )
}