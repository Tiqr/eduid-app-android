package nl.eduid.ui

import androidx.compose.foundation.layout.Column
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
import nl.eduid.ui.theme.ColorScale_Gray_Black
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.ColorMain_Green_400


@Composable
fun TwoColorTitle(
    firstPart: String,
    secondPart: String,
    modifier: Modifier = Modifier,
    isEditTitle: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge.copy(
        textAlign = TextAlign.Start
    ),
) = Column(
    modifier = modifier.padding(vertical = 2.dp)
) {
    Text(
        text = firstPart,
        style = textStyle.copy(color = if (isEditTitle) ColorScale_Gray_Black else ColorMain_Green_400),
    )
    Text(
        text = secondPart,
        style = textStyle.copy(color = if (isEditTitle) ColorMain_Green_400 else ColorScale_Gray_Black),
    )
}

@Preview
@Composable
private fun Preview_BulletPoints() {
    EduidAppAndroidTheme {
        BulletPoint(
            text = "A detailed bullet point",
            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
        )
    }
}