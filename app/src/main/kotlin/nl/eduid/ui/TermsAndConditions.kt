package nl.eduid.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun CheckToSAndPrivacyPolicy(
    hasAcceptedToC: Boolean,
    modifier: Modifier = Modifier,
    onAcceptChange: (Boolean) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = hasAcceptedToC,
            onCheckedChange = onAcceptChange,
            modifier = Modifier.height(16.dp).width(16.dp)
        )
        val uriHandler = LocalUriHandler.current
        val privacyPolicy = stringResource(R.string.tc_understand)
        val fullText = stringResource(R.string.tc_full_text)
        val annotatedString = with(AnnotatedString.Builder(stringResource(R.string.tc_agree))) {
            append(" ")
            pushStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline))
            append(stringResource(R.string.tc_terms))
            pop()
            append(" ")
            append(privacyPolicy)
            append(" ")
            pushStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline))
            append(stringResource(R.string.tc_privacy_policy))
            toAnnotatedString()
        }
        ClickableText(
            text = annotatedString,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.wrapContentWidth().padding(start = 12.dp),
            onClick = {
                val privacyPosition = fullText.indexOf(privacyPolicy)
                if (it < privacyPosition) {
                    uriHandler.openUri("https://sisaa.com/legal-terms/")
                } else {
                    uriHandler.openUri("https://sisaa.com/privacy-notice/")
                }
            }
        )
    }
}


@Preview
@Composable
private fun Preview_CheckToSAndPrivacyPolicy() {
    EduidAppAndroidTheme {
        CheckToSAndPrivacyPolicy(
            true, Modifier, {}
        )
    }
}