package nl.eduid.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.MainSurfGreen
import nl.eduid.ui.theme.TextBlack


@Composable
fun ColumnScope.TwoColorTitle(
    firstPart: String,
    secondPart: String,
) {
    Text(
        text = firstPart,
        style = MaterialTheme.typography.titleLarge.copy(color = TextBlack),
        modifier = Modifier.align(Alignment.Start)
    )
    Text(
        text = secondPart,
        style = MaterialTheme.typography.titleLarge.copy(color = MainSurfGreen),
        modifier = Modifier.align(Alignment.Start)
    )
}

@Preview
@Composable
private fun Preview_TwoColorTitle() {
    EduidAppAndroidTheme {
        Column {
            TwoColorTitle(
                firstPart = "Edit",
                secondPart = "Something",
            )
        }
    }
}