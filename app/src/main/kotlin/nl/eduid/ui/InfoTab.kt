package nl.eduid.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.eduid.R
import nl.eduid.ui.theme.BlueButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGrayScale

@Composable
fun InfoTab(
    header: String = "", title: String, subtitle: String, onClick: () -> Unit, enabled: Boolean = true, isDropdown: Boolean = false, @DrawableRes endIcon: Int = 0, @DrawableRes startIcon: Int = 0,
) {
    if (header.isNotBlank()) {
        Text(
            text = header,
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
    Spacer(Modifier.height(6.dp))
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(width = (if (enabled) 3.dp else 1.dp), color = (if (enabled) BlueButton else TextGrayScale))
            .sizeIn(minHeight = 72.dp)
            .fillMaxWidth()
            .padding(start = 18.dp, end = 10.dp, top = 12.dp, bottom = 12.dp)
            .clickable { }
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .align(Alignment.CenterStart)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    textAlign = TextAlign.Start,
                    color = TextGrayScale,
                ),
            )
        }
        Image(
            painter = painterResource(endIcon),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth(0.15f)
                .align(Alignment.CenterEnd)
        )
    }
    Spacer(Modifier.height(16.dp))
}

@Preview
@Composable
private fun PreviewInfoTab() {
    EduidAppAndroidTheme {
        InfoTab(title = "OK", subtitle = "OK", onClick = { }, enabled = true, endIcon = R.drawable.shield_tick_blue)
    }
}