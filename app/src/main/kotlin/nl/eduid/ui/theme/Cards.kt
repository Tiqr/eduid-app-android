package nl.eduid.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R

@Composable
fun LinkAccountCard(
    title: Int,
    subtitle: Int,
    enabled: Boolean = true,
    addLinkToAccount: () -> Unit = {},
) {
    OutlinedButton(
        enabled = enabled,
        onClick = addLinkToAccount,
        shape = RoundedCornerShape(CornerSize(6.dp)),
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 72.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Start,
                    color = ButtonTextGrey,
                    fontWeight = FontWeight.Bold,
                )
            )
            Text(
                text = stringResource(subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = ButtonTextGrey,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic
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
            title = R.string.personalinfo_add_role_institution,
            subtitle = R.string.personalinfo_add_via,
        )
    }
}