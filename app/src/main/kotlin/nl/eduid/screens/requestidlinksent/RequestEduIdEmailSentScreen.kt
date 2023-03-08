package nl.eduid.screens.requestidlinksent

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.ScaffoldWithTopBarBackButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGrey
import timber.log.Timber

@Composable
fun RequestEduIdEmailSentScreen(
    requestId: () -> Unit,
    onBackClicked: () -> Unit,
    userEmail: String,
) = ScaffoldWithTopBarBackButton(
    onBackClicked = onBackClicked, modifier = Modifier
) {
    val context = LocalContext.current
    val gmailIntent = context.packageManager.getLaunchIntentForPackage("com.google.android.gm")
    val outlookIntent =
        context.packageManager.getLaunchIntentForPackage("com.microsoft.office.outlook")
    LaunchedEffect(true) {
        Timber.e("Available launch intent for GM: ${gmailIntent != null}. Outlook?: ${outlookIntent != null}")
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.request_id_link_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
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
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
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
        if (gmailIntent != null) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .clickable {
                            context.startActivity(gmailIntent)
                        }
                        .weight(1f),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gmail_icon_small),
                        contentDescription = "",
                        modifier = Modifier.size(width = 22.dp, height = 22.dp)
                    )
                    Text(
                        text = stringResource(R.string.request_id_link_open_gmail),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

            }
            if (outlookIntent != null) {
                Row(
                    modifier = Modifier
                        .clickable {
                            context.startActivity(outlookIntent)
                        },
                    horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.outlook_icon_small),
                        contentDescription = "",
                        modifier = Modifier.size(width = 22.dp, height = 22.dp)
                    )
                    Text(
                        text = stringResource(R.string.request_id_link_open_outlook),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Text(
            text = stringResource(R.string.request_id_link_spam_text),
            style = MaterialTheme.typography.bodyMedium.copy(color = TextGrey),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview
@Composable
private fun PreviewEnroll() {
    EduidAppAndroidTheme {
        RequestEduIdEmailSentScreen({}, {}, "test@email.com")
    }
}