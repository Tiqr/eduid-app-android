package nl.eduid.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun OverviewScreen(
    gotoTestSettings: () -> Unit = {},
    gotoFeatureFlags: () -> Unit = {},
    goToTheme: () -> Unit = {},
) = EduIdTopAppBar(
    withBackIcon = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider(thickness = 2.dp)
        Text(text = "Go to Test Settings >>", style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.SemiBold
        ), modifier = Modifier
            .fillMaxWidth()
            .clickable { gotoTestSettings() })
        HorizontalDivider(thickness = 2.dp)
        Text(text = "Go to Feature Flags >>", style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.SemiBold
        ), modifier = Modifier
            .fillMaxWidth()
            .clickable { gotoFeatureFlags() })
        HorizontalDivider(thickness = 2.dp)
        Text(text = "Check theme >>", style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.SemiBold
        ), modifier = Modifier
            .fillMaxWidth()
            .clickable { goToTheme() })
        HorizontalDivider(thickness = 2.dp)
    }
}


@Preview
@Composable
private fun Preview_OverviewScreen() {
    EduidAppAndroidTheme {
        OverviewScreen()
    }
}