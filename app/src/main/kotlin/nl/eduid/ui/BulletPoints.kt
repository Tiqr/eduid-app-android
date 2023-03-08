package nl.eduid.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.ui.theme.EduidAppAndroidTheme


@Composable
fun BulletPoint(
    text: String, textStyle: TextStyle, modifier: Modifier = Modifier
) = Row(
    modifier = modifier.padding(vertical = 2.dp)
) {
    Text(
        text = "  •  ",
        style = textStyle,
    )
    Text(
        text = text,
        style = textStyle,
    )
}

@Preview
@Composable
private fun Preview_BulletPoints() {
    EduidAppAndroidTheme {
        BulletPoint(text = "A detailed bullet point",
            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
            modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth()
        )
    }
}