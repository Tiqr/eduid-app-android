package nl.eduid.screens.resetpassword

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.ButtonBlue
import nl.eduid.ui.theme.ButtonBorderGrey
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGrey

@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel,
    onResetPasswordClicked: () -> Unit,
    goBack: () -> Unit,
) {
    ResetPasswordScreenContent(
        onResetPasswordClicked = onResetPasswordClicked,
        goBack = goBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreenContent(
    onResetPasswordClicked: () -> Unit,
    goBack: () -> Unit,
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
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 26.dp, end = 26.dp)
        ) {
            val (body, bottomColumn) = createRefs()
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .constrainAs(body) {
                        linkTo(parent.top, bottomColumn.top, bias = 0F)
                    }
                    .verticalScroll(rememberScrollState())

            ) {
                Spacer(Modifier.height(36.dp))
                Text(
                    style = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.Start,
                        color = ButtonGreen
                    ),
                    text = stringResource(R.string.reset_password_title),
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                    text = stringResource(R.string.reset_password_subtitle),
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(Modifier.height(36.dp))
            }
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.constrainAs(bottomColumn) {
                    bottom.linkTo(parent.bottom, margin = 24.dp)
                },
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PrimaryButton(
                            modifier = Modifier.widthIn(min = 140.dp),
                            text = stringResource(R.string.reset_password_cancel_button),
                            onClick = goBack,
                            buttonBackgroundColor = Color.Transparent,
                            buttonTextColor = TextGrey,
                            buttonBorderColor = ButtonBorderGrey,
                        )
                        PrimaryButton(
                            modifier = Modifier.widthIn(min = 140.dp),
                            text = stringResource(R.string.reset_password_confirm_button),
                            onClick = onResetPasswordClicked,
                            buttonBackgroundColor = ButtonBlue,
                            buttonTextColor = Color.White,
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun PreviewResetPasswordScreenContent() = EduidAppAndroidTheme {
    ResetPasswordScreenContent(
        onResetPasswordClicked = { },
        goBack = { },
    )
}