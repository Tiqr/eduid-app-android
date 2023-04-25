package nl.eduid.screens.homepage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import nl.eduid.ui.AlertDialogWithTwoButton
import nl.eduid.ui.PrimaryButtonWithIcon
import nl.eduid.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageWithAccountContent(
    isAuthorizedForDataAccess: Boolean,
    shouldPromptAuthorization: Unit?,
    onActivityClicked: () -> Unit,
    onPersonalInfoClicked: () -> Unit,
    onSecurityClicked: () -> Unit,
    onScanForAuthorization: () -> Unit,
    launchOAuth: () -> Unit,
    promptAuthorization: () -> Unit = {},
    clearAuth: () -> Unit = {},
) = Scaffold(
    topBar = {
        CenterAlignedTopAppBar(
            modifier = Modifier.padding(top = 42.dp, start = 26.dp, end = 26.dp),
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
    if (shouldPromptAuthorization != null) {
        AlertDialogWithTwoButton(
            title = stringResource(R.string.app_not_authorized),
            explanation = stringResource(id = R.string.app_not_authorized_explanation),
            dismissButtonLabel = stringResource(R.string.button_cancel),
            confirmButtonLabel = stringResource(R.string.button_allow),
            onDismiss = clearAuth,
            onConfirm = {
                clearAuth()
                launchOAuth()
            }
        )
    }
    ConstraintLayout(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        val (title, bottomColumn) = createRefs()

        Text(
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = TextGreen)) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 1.dp)
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
                    PrimaryButtonWithIcon(
                        text = stringResource(R.string.home_with_account_scan), onClick = {
                            onScanForAuthorization()
                        }, icon = R.drawable.homepage_scan_icon,
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButtonWithIcon(
                        text = stringResource(R.string.home_with_account_personal_info), onClick = {
                            if (isAuthorizedForDataAccess) {
                                onPersonalInfoClicked()
                            } else {
                                promptAuthorization()
                            }
                        }, icon = R.drawable.homepage_info_icon,
                        modifier = Modifier.weight(1f)
                    )

                    PrimaryButtonWithIcon(
                        text = stringResource(R.string.home_with_account_security), onClick = {
                            if (isAuthorizedForDataAccess) {
                                onSecurityClicked()
                            } else {
                                promptAuthorization()
                            }
                        }, icon = R.drawable.homepage_security_icon,
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButtonWithIcon(
                        text = stringResource(R.string.home_with_account_activity), onClick = {
                            if (isAuthorizedForDataAccess) {
                                onActivityClicked()
                            } else {
                                promptAuthorization()
                            }
                        }, icon = R.drawable.homepage_activity_icon,
                        modifier = Modifier.weight(1f)
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
    HomePageWithAccountContent(isAuthorizedForDataAccess = true,
        shouldPromptAuthorization = null,
        onActivityClicked = {},
        onPersonalInfoClicked = {},
        onSecurityClicked = {},
        onScanForAuthorization = {},
        launchOAuth = {},
        promptAuthorization = {},
        clearAuth = {})
}