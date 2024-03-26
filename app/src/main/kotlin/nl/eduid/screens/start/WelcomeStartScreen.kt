package nl.eduid.screens.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun WelcomeStartScreen(
    viewModel: WelcomeStartViewModel,
    onNext: (Boolean) -> Unit,
) = EduIdTopAppBar(
    withBackIcon = false
) {
    val uiState by viewModel.uiState.observeAsState(UiState())

    WelcomeStartContent(uiState, padding = it) {
        onNext(uiState.isAccountLinked)
    }
}

@Composable
private fun WelcomeStartContent(
    uiState: UiState,
    padding: PaddingValues = PaddingValues(),
    onNext: () -> Unit = {},
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        val (bottomButton) = createRefs()
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.WelcomeToApp_Title_COPY),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.green1icon),
                    contentDescription = "",
                    modifier = Modifier.size(width = 32.dp, height = 32.dp),
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge, text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.WelcomeToApp_Quickly_Highlight_COPY))
                        }
                        append(" ")
                        append(stringResource(R.string.WelcomeToApp_Quickly_Text_COPY))
                    }, modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.green2icon),
                    contentDescription = "",
                    modifier = Modifier.size(width = 32.dp, height = 32.dp),
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge, text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.WelcomeToApp_ViewWhat_Highlight_COPY))
                        }
                        append(" ")
                        append(stringResource(R.string.WelcomeToApp_ViewWhat_Text_COPY))
                    }, modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.green3icon),
                    contentDescription = "",
                    modifier = Modifier.size(width = 32.dp, height = 32.dp),
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge, text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.WelcomeToApp_VerifyYour_Highlight_COPY))
                        }
                        append(" ")
                        append(stringResource(R.string.WelcomeToApp_VerifyYour_Text_COPY))
                    }, modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Image(
                painter = painterResource(R.drawable.start_screen_icon),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 190.dp, height = 190.dp),
            )
        }

        PrimaryButton(
            text = stringResource(R.string.WelcomeToApp_GotItButton_COPY),
            enabled = !uiState.isLoading,
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomButton) {
                    bottom.linkTo(parent.bottom)
                },
        )
    }
}

@Preview()
@Composable
private fun PreviewStartScreen() {
    EduidAppAndroidTheme {
        WelcomeStartContent(UiState(true, false)) {}
    }
}