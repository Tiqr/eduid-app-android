package nl.eduid.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.theme.ButtonTextGrey
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.MainSurfGreen

@Composable
fun PrimaryButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true,
    buttonBackgroundColor: Color = MainSurfGreen,
    buttonTextColor: Color = Color.White,
    buttonBorderColor: Color = Color.Transparent,
) = Button(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(CornerSize(6.dp)),
    border = BorderStroke(1.dp, buttonBorderColor),
    colors = ButtonDefaults.buttonColors(containerColor = buttonBackgroundColor),
    modifier = modifier.requiredHeight(height = 48.dp)
) {
    Text(
        text = text,
        overflow = TextOverflow.Ellipsis,
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
    modifier = modifier
        .clickable {
            onClick()
        },
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(CornerSize(6.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = MainSurfGreen),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier
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
        textAlign = TextAlign.Center,
        maxLines = 2,
        softWrap = true,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSecondary),
    )
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = ButtonTextGrey,
    enabled: Boolean = true,
) = OutlinedButton(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(CornerSize(6.dp)),
    modifier = modifier.sizeIn(minHeight = 48.dp)
) {
    Text(
        text = text,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = color, fontWeight = FontWeight.SemiBold
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

@Preview(locale = "nl")
@Composable
private fun Preview_PrimaryButtonWithIcon() {
    EduidAppAndroidTheme {
        PrimaryButtonWithIcon(
            text = stringResource(R.string.HomeView_PersonalInfoButton_COPY),
            icon = R.drawable.homepage_scan_icon,
            onClick = { })
    }
}

@Preview
@Composable
private fun Preview_SecondaryButton() {
    EduidAppAndroidTheme {
        SecondaryButton(text = "OK", onClick = { /*TODO*/ })
    }
}