package nl.eduid.screens.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import nl.eduid.screens.requestidlinksent.RequestIdLinkSentScreen
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    onNext: () -> Unit,
) = Scaffold(modifier = Modifier.systemBarsPadding(), topBar = {
    CenterAlignedTopAppBar(modifier = Modifier
        .padding(top = 52.dp, bottom = 40.dp)
        .padding(horizontal = 10.dp), navigationIcon = {
    }, title = {
        Image(
            painter = painterResource(R.drawable.logo_eduid_big),
            contentDescription = "",
            modifier = Modifier.size(width = 122.dp, height = 46.dp),
            alignment = Alignment.Center
        )
    })
}) { paddingValues ->
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {
        val (content, bottomButton, bottomSpacer) = createRefs()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.start_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        PrimaryButton(
            text = stringResource(R.string.start_button),
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomButton) {
                    bottom.linkTo(bottomSpacer.top)
                },
        )
        Spacer(
            Modifier
                .height(40.dp)
                .constrainAs(bottomSpacer) {
                    bottom.linkTo(parent.bottom)
                },
        )
    }
}

@Preview()
@Composable
private fun PreviewEnroll() {
    EduidAppAndroidTheme {
        StartScreen({})
    }
}