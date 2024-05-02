package nl.eduid.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import nl.eduid.ui.theme.ColorAlertRed
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun PrimaryButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true,
    buttonBackgroundColor: Color = MaterialTheme.colorScheme.onSecondary,
) = Button(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(CornerSize(6.dp)),
    colors = ButtonDefaults.buttonColors(
        containerColor = buttonBackgroundColor,
        disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
        disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ),
    modifier = modifier.requiredHeight(height = 48.dp)
) {
    Text(
        text = text,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
    )
}

@Composable
fun PrimaryButtonWithIcon(
    text: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier.clickable { onClick() }, horizontalAlignment = Alignment.CenterHorizontally
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(CornerSize(6.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSecondary),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.size(height = 60.dp, width = 60.dp)
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
        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimary),
    )
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) = OutlinedButton(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(CornerSize(6.dp)),
    colors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ),
    modifier = modifier.sizeIn(minHeight = 48.dp),
) {
    Text(
        text = text,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview_PrimaryButton() {
    EduidAppAndroidTheme {
        EduIdTopAppBar {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(it)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    PrimaryButton(text = "Primary OK", onClick = { })
                    PrimaryButton(
                        text = "Primary DISABLED",
                        onClick = { },
                        enabled = false
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    PrimaryButton(
                        text = "Delete OK",
                        onClick = { },
                        buttonBackgroundColor = ColorAlertRed
                    )
                    PrimaryButton(
                        text = "Delete DISABLED",
                        onClick = { },
                        enabled = false,
                        buttonBackgroundColor = ColorAlertRed
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    SecondaryButton(text = "Secondary", onClick = { })
                    SecondaryButton(text = "Secondary DISABLED", onClick = { }, enabled = false)
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    SecondaryButton(
                        text = stringResource(R.string.Email_Cancel_COPY),
                        onClick = {},
                        modifier = Modifier.widthIn(min = 140.dp),
                    )
                    PrimaryButton(
                        text = "Delete",
                        buttonBackgroundColor = ColorAlertRed,
                        onClick = {},
                        modifier = Modifier.widthIn(min = 140.dp),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    SecondaryButton(
                        text = stringResource(R.string.Email_Cancel_COPY),
                        onClick = {},
                        modifier = Modifier.widthIn(min = 140.dp),
                    )
                    PrimaryButton(
                        text = "Save",
                        onClick = {},
                        modifier = Modifier.widthIn(min = 140.dp),
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onSurface)
                ) {
                    PrimaryButtonWithIcon(
                        text = stringResource(R.string.HomeView_ScanQRButton_COPY),
                        onClick = {},
                        icon = R.drawable.homepage_scan_icon,
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButtonWithIcon(
                        text = stringResource(R.string.HomeView_PersonalInfoButton_COPY),
                        onClick = {},
                        icon = R.drawable.homepage_info_icon,
                        modifier = Modifier.weight(1f)
                    )

                    PrimaryButtonWithIcon(
                        text = stringResource(R.string.HomeView_SecurityButton_COPY),
                        onClick = {},
                        icon = R.drawable.homepage_security_icon,
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButtonWithIcon(
                        text = stringResource(R.string.HomeView_ActivityButton_COPY),
                        onClick = {},
                        icon = R.drawable.homepage_activity_icon,
                        modifier = Modifier.weight(1f)
                    )
                }

            }
        }
    }
}