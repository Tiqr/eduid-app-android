package nl.eduid.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.ButtonTextGrey
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun PrimaryButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true,
    buttonBackgroundColor: Color = ButtonGreen,
    buttonTextColor: Color = Color.White,
    buttonBorderColor: Color = Color.Transparent,
) = Button(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(CornerSize(6.dp)),
    border = BorderStroke(1.dp, buttonBorderColor),
    colors = ButtonDefaults.buttonColors(containerColor = buttonBackgroundColor),
    modifier = modifier.sizeIn(minHeight = 48.dp)
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = buttonTextColor, fontWeight = FontWeight.SemiBold
        ),
    )
}

@Composable
fun PrimaryButtonWithIcon(
    text: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) = Column(
    modifier = Modifier
        .clickable {
            onClick()
        },
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(CornerSize(6.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
            .size(height = 60.dp, width = 60.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = text,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(48.dp)
                .widthIn(min = 32.dp, max = 48.dp)
        )
    }
    Spacer(Modifier.height(12.dp))
    Text(
        text = text,
        textAlign = TextAlign.Justify,
        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSecondary),
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
            color = ButtonTextGrey, fontWeight = FontWeight.SemiBold
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
private fun Preview_PrimaryButtonWithIcon() {
    EduidAppAndroidTheme {
        PrimaryButtonWithIcon(
            text = "Scan QR",
            icon = R.drawable.homepage_scan_icon,
            onClick = {  })
    }
}

@Preview
@Composable
private fun Preview_SecondaryButton() {
    EduidAppAndroidTheme {
        SecondaryButton(text = "OK", onClick = { /*TODO*/ })
    }
}