package nl.eduid.ui

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context -> TextView(context).apply {
            movementMethod = LinkMovementMethod.getInstance()
        } },
        update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) }
    )
}


@Preview
@Composable
private fun HtmlText_Preview() {
    EduidAppAndroidTheme {
        HtmlText(html = "HTML<br><br>description")
    }
}
