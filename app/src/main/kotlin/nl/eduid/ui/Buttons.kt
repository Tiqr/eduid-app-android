package nl.eduid.ui

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.ButtonTextGrey
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun PrimaryButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true
) = Button(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(CornerSize(6.dp)),
    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
    modifier = modifier.sizeIn(minHeight = 48.dp)
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        ),
    )
}

@Composable
fun SecondaryButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true
) = OutlinedButton(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(CornerSize(6.dp)),
    modifier = modifier.sizeIn(minHeight = 48.dp)
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = ButtonTextGrey,
            fontWeight = FontWeight.SemiBold
        ),
    )
}

@Preview
@Composable
private fun Preview_PrimaryButton() {
    EduidAppAndroidTheme {
        PrimaryButton(text = "OK", onClick = { /*TODO*/ })
    }
}

@Preview
@Composable
private fun Preview_SecondaryButton() {
    EduidAppAndroidTheme {
        SecondaryButton(text = "OK", onClick = { /*TODO*/ })
    }
}