package nl.eduid.screens.authorize

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGreen
import nl.eduid.ui.theme.findActivity

@Composable
fun AuthenticationCompletedScreen(goHome: () -> Unit = {}) = EduIdTopAppBar(
    withBackIcon = false
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start, color = TextGreen
            ), text = stringResource(R.string.PinAndBioMetrics_LoginRequest_COPY), modifier = Modifier.fillMaxWidth()
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_authorize_confirmed),
                contentDescription = "",
                modifier = Modifier.wrapContentSize(),
                alignment = Alignment.Center
            )

            Text(
                text = stringResource(R.string.Profile_YouAreLoggedIn_COPY),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextGreen
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, false)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            text = stringResource(R.string.button_ok),
            onClick = {
                goHome()
                context.findActivity().finish()
            },
        )
    }
}

@Preview
@Composable
private fun PreviewAuthorizeConfirmedScreen() = EduidAppAndroidTheme {
    AuthenticationCompletedScreen()
}