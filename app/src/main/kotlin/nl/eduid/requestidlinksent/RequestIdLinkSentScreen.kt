package nl.eduid.requestidlinksent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.ScaffoldWithTopBarBackButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack
import nl.eduid.ui.theme.TextGrey

@Composable
fun RequestIdLinkSentScreen(
    requestId: () -> Unit,
    onBackClicked: () -> Unit,
    userEmail: String,
) = ScaffoldWithTopBarBackButton(
    onBackClicked = onBackClicked,
    modifier = Modifier
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (content, bottomButton, bottomSpacer) = createRefs()

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(content) {
                    top.linkTo(parent.top)
                }
        ) {

            Text(
                text = stringResource(R.string.request_id_link_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start,
                    color = TextBlack
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            val annotatedString =
                with(AnnotatedString.Builder(stringResource(R.string.request_id_link_header_text))) {
                    append(" ")
                    pushStyle(SpanStyle(color = Color.Blue))
                    append(userEmail)
                    pop()
                    toAnnotatedString()
                }

            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            CircularProgressIndicator(
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val (gmail, outlook) = createRefs()
                Row(modifier = Modifier
                    .constrainAs(gmail) {
                        start.linkTo(parent.start)
                    }) {
                    Image(
                        painter = painterResource(id = R.drawable.gmail_icon_small),
                        contentDescription = "",
                        modifier = Modifier
                            .size(width = 22.dp, height = 22.dp)
                    )
                    Text(
                        text = stringResource(R.string.request_id_link_open_gmail),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(start = 6.dp)
                    )
                }

                Row(modifier = Modifier
                    .constrainAs(outlook) {
                        end.linkTo(parent.end)
                    }) {
                    Image(
                        painter = painterResource(id = R.drawable.outlook_icon_small),
                        contentDescription = "",
                        modifier = Modifier
                            .size(width = 22.dp, height = 22.dp)
                    )
                    Text(
                        text = stringResource(R.string.request_id_link_open_outlook),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(start = 6.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Text(
                text = stringResource(R.string.request_id_link_spam_text),
                style = MaterialTheme.typography.bodyMedium.copy(color = TextGrey),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )



        }
        PrimaryButton(
            text = stringResource(R.string.request_id_screen_create_id_button),
            onClick = requestId,
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
        RequestIdLinkSentScreen({}, {}, "test@email.com")
    }
}




