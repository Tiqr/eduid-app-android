package nl.eduid.screens.authorize

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGreen
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
        onLogin = { onLogin(authChallenge) },
        onCancel = onCancel
    )
}

@Composable
private fun RequestAuthenticationContent(
    loginToService: String?,
    onLogin: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    if (loginToService == null) {


    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start, color = TextGreen
                ),
                text = stringResource(R.string.authorize_title),
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                val loginQuestion = buildAnnotatedString {
                    pushStyle(
                        MaterialTheme.typography.titleLarge.copy(
                            color = TextGreen
                        ).toSpanStyle()
                    )
                    append(stringResource(R.string.authorize_subtitle01))
                    pop()
                    append("\n")
                    pushStyle(
                        MaterialTheme.typography.titleLarge.copy(
                            textAlign = TextAlign.Center
                        ).toSpanStyle()
                    )
                    append(
                        stringResource(
                            R.string.authorize_subtitle02, loginToService
                        )
                    )
                }

                Text(
                    text = loginQuestion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
            ) {
                SecondaryButton(
                    modifier = Modifier.widthIn(min = 140.dp),
                    text = stringResource(R.string.button_cancel),
                    onClick = onCancel,
                )
                PrimaryButton(
                    modifier = Modifier.widthIn(min = 140.dp),
                    text = stringResource(R.string.authorize_login_button),
                    onClick = onLogin,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewRequestAuthorizeContent() = EduidAppAndroidTheme {
    RequestAuthenticationContent("3rd party service")
}