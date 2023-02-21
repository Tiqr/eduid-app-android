package nl.eduid.screens.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
            Text(
                text = stringResource(R.string.start_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.green1icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(width = 32.dp, height = 32.dp),
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.start_item_one_bold))
                        }
                        append(" ")
                        append(stringResource(R.string.start_item_one_regular))
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.green2icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(width = 32.dp, height = 32.dp),
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.start_item_two_bold))
                        }
                        append(" ")
                        append(stringResource(R.string.start_item_two_regular))
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.green3icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(width = 32.dp, height = 32.dp),
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.start_item_three_bold))
                        }
                        append(" ")
                        append(stringResource(R.string.start_item_three_regular))
                    },
                    modifier = Modifier.padding(start = 8.dp)
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
private fun PreviewStartScreen() {
    EduidAppAndroidTheme {
        StartScreen({})
    }
}