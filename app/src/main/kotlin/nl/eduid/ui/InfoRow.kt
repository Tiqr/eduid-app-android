package nl.eduid.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nl.eduid.ui.theme.BlueText
import nl.eduid.ui.theme.TextBlack

@Composable
fun InfoRow(label: String, value: String = ""){
    Row(
        modifier = Modifier.fillMaxWidth()
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
    HorizontalDivider(color = TextBlack, thickness = 1.dp)
    Spacer(Modifier.height(12.dp))
}
