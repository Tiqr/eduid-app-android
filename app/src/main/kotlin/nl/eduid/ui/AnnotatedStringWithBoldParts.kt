package nl.eduid.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import nl.eduid.ui.theme.EduidAppAndroidTheme

fun annotatedStringWithBoldParts(
    text: String,
    vararg boldParts: String,
    boldWeight: FontWeight = FontWeight.SemiBold
) : AnnotatedString {
    val annotatedString = AnnotatedString.Builder(text)
    boldParts.forEach { boldPart ->
        val startIndex = text.indexOf(boldPart)
        if (startIndex >= 0) {
            annotatedString.addStyle(SpanStyle(fontWeight = boldWeight), startIndex, boldPart.length)
        }
    }
    return annotatedString.toAnnotatedString()
}

@Preview
@Composable
private fun Preview_AnnotatedStringWithBoldPart() {
    EduidAppAndroidTheme {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = annotatedStringWithBoldParts(text = "This part of the text is bold, this part is not", "This part of the text is bold,"),
        )
    }
}