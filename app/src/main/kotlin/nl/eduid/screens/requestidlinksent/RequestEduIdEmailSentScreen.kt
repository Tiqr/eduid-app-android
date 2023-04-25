package nl.eduid.screens.requestidlinksent

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.graphs.RequestEduIdLinkSent.ADD_PASSWORD_REASON
import nl.eduid.graphs.RequestEduIdLinkSent.CHANGE_PASSWORD_REASON
import nl.eduid.graphs.RequestEduIdLinkSent.LOGIN_REASON
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGrey

@Composable
fun RequestEduIdEmailSentScreen(
    userEmail: String,
    reason: String = LOGIN_REASON,
    onBackClicked: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = onBackClicked
) {
    val context = LocalContext.current
    val reasonExplanation by remember {
        derivedStateOf {
            when (reason) {
                LOGIN_REASON -> R.string.request_id_link_header_text
                ADD_PASSWORD_REASON -> R.string.reset_password_add_pass_email_sent
                CHANGE_PASSWORD_REASON -> R.string.reset_password_change_pass_email_sent
                else -> R.string.request_id_link_header_text
            }
        }
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
            with(AnnotatedString.Builder(stringResource(reasonExplanation))) {
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
        PrimaryButton(
            text = stringResource(R.string.request_id_link_open_email_client), onClick = {
                val intent =
                    Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_EMAIL)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        )

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
        RequestEduIdEmailSentScreen("test+test@email.com") {}
    }
}