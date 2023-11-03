package nl.eduid.screens.contbrowser

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton

@Preview
@Composable
fun ContinueInBrowserScreen(
    goHome: () -> Unit = {},
) = EduIdTopAppBar(
    withBackIcon = false
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        val (bottomButton) = createRefs()
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.recovery_browser_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(40.dp))

            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(R.string.recovery_browser_explain),
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(Modifier.height(16.dp))

            Image(
                painter = painterResource(R.drawable.recover_in_browser),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 190.dp, height = 190.dp),
            )
        }

        PrimaryButton(
            text = stringResource(R.string.WelcomeToApp_GotItButton_COPY),
            onClick = goHome,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomButton) {
                    bottom.linkTo(parent.bottom)
                },
        )
    }
}