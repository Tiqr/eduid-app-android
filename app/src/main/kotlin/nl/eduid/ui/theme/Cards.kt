package nl.eduid.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R

@Composable
fun LinkAccountCard(
    title: Int,
    enabled: Boolean = true,
    addLinkToAccount: () -> Unit = {},
) {
    OutlinedButton(
        enabled = enabled,
        onClick = addLinkToAccount,
        shape = RoundedCornerShape(CornerSize(6.dp)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 72.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                )
            )
        }
        Image(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = "",
        )
    }
}


@Preview
@Composable
private fun Preview_LinkAccountCard() {
    EduidAppAndroidTheme {
        LinkAccountCard(
            title = R.string.Profile_AddAnOrganisation_COPY,
        )
    }
}