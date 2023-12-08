package nl.eduid.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.charlex.compose.material3.HtmlText
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
            modifier = Modifier
                .height(16.dp)
                .width(16.dp)
        )
        HtmlText(
            text = stringResource(id = R.string.LinkFromInstitution_AgreeWithTerms_COPY),
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = 12.dp)
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