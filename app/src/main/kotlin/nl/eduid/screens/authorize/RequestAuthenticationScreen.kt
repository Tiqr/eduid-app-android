package nl.eduid.screens.authorize

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.theme.ColorMain_Green_400
import nl.eduid.ui.theme.EduidAppAndroidTheme
import org.tiqr.data.model.AuthenticationChallenge

@Composable
fun RequestAuthenticationScreen(
    viewModel: EduIdAuthenticationViewModel,
    onLogin: (AuthenticationChallenge?) -> Unit,
    onCancel: () -> Unit,
) = EduIdTopAppBar(
    withBackIcon = false
) {
    val authChallenge by viewModel.challenge.observeAsState(null)
    RequestAuthenticationContent(
        loginToService = authChallenge?.serviceProviderDisplayName,
        padding = it,
        onLogin = { onLogin(authChallenge) },
        onCancel = onCancel,
    )
}

@Composable
private fun RequestAuthenticationContent(
    loginToService: String?,
    padding: PaddingValues = PaddingValues(),
    onLogin: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    if (loginToService == null) {

    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start, color = ColorMain_Green_400
                ),
                text = stringResource(R.string.PinAndBioMetrics_LoginRequest_COPY),
                modifier = Modifier.fillMaxWidth()
            )
            val loginQuestion = buildAnnotatedString {
                pushStyle(
                    MaterialTheme.typography.titleLarge.copy(
                        color = ColorMain_Green_400
                    ).toSpanStyle()
                )
                append(stringResource(R.string.PinAndBioMetrics_DoYouWantToLogInTo_COPY))
                pop()
                append("\n")
                pushStyle(
                    MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.Center
                    ).toSpanStyle()
                )
                append(loginToService)
                append("?")
            }

            Text(
                text = loginQuestion,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .weight(1f, false)
                    .padding(bottom = 24.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.Button_Cancel_COPY),
                    onClick = onCancel,
                    modifier = Modifier.widthIn(min = 140.dp),
                )
                PrimaryButton(
                    modifier = Modifier.widthIn(min = 140.dp),
                    text = stringResource(R.string.PinAndBioMetrics_SignIn_COPY),
                    onClick = onLogin,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewRequestAuthorizeContent() = EduidAppAndroidTheme {
    RequestAuthenticationContent("3rd party service")
}