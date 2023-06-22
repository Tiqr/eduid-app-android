package nl.eduid.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.theme.ButtonBlue
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun OverviewScreen(
    gotoTestSettings: () -> Unit = {},
    gotoFeatureFlags: () -> Unit = {},
) = EduIdTopAppBar(
    withBackIcon = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = ButtonBlue
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Go to Test Settings >>",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { gotoTestSettings() }
        )
        Spacer(Modifier.height(8.dp))
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = ButtonBlue
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Go to Feature Flags >>",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { gotoFeatureFlags() }
        )
        Spacer(Modifier.height(8.dp))
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = ButtonBlue
        )
        Spacer(Modifier.height(8.dp))

    }
}


@Preview
@Composable
private fun Preview_OverviewScreen() {
    EduidAppAndroidTheme {
        OverviewScreen()
    }
}