package nl.eduid.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.ui.theme.BlueText
import nl.eduid.ui.theme.ColorScale_Gray_Black
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun InfoRowOld(label: String, value: String = "") {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Start,
                color = BlueText,
            ),
        )
        if (value.isNotEmpty()) {
            Text(
                modifier = Modifier.weight(1f),
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
        }
    }
    Spacer(Modifier.height(12.dp))
    HorizontalDivider(color = ColorScale_Gray_Black, thickness = 1.dp)
    Spacer(Modifier.height(12.dp))
}

@Composable
fun InfoRow(label: String, value: String? = null) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
    )
    value?.let {
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Preview
@Composable
private fun Preview_InfoRow() = EduidAppAndroidTheme {
    InfoRow(label = "Some label:", value = "Some bold info")
}