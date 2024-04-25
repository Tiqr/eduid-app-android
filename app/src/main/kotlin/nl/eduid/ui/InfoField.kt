package nl.eduid.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.eduid.R
import nl.eduid.ui.theme.BlueButton
import nl.eduid.ui.theme.ColorGrayScale500
import nl.eduid.ui.theme.EduidAppAndroidTheme
import java.util.Locale

@Composable
fun InfoField(
    title: String,
    subtitle: AnnotatedString,
    onClick: () -> Unit = {},
    label: String = "",
    capitalizeTitle: Boolean = true,
    @DrawableRes endIcon: Int = R.drawable.edit_icon,
) = Column(modifier = Modifier.fillMaxWidth()) {
    if (label.isNotBlank()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        Spacer(Modifier.height(6.dp))
    }
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 3.dp, color = BlueButton
            )
            .sizeIn(minHeight = 72.dp)
            .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 12.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = if (capitalizeTitle) {
                    title.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                } else {
                    title
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Start, fontWeight = FontWeight.Bold, lineHeight = 20.sp
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    textAlign = TextAlign.Start,
                    color = ColorGrayScale500,
                ),
            )
        }
        Image(
            painter = painterResource(endIcon),
            contentDescription = "",
            modifier = Modifier.padding(start = 12.dp),
            alignment = Alignment.Center
        )
    }
}

@Composable
fun InfoField(
    title: String,
    subtitle: String,
    onClick: () -> Unit = {},
    label: String = "",
    capitalizeTitle: Boolean = true,
    @DrawableRes endIcon: Int = R.drawable.edit_icon,
) = Column(modifier = Modifier.fillMaxWidth()) {
    if (label.isNotBlank()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        Spacer(Modifier.height(6.dp))
    }
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 3.dp, color = BlueButton
            )
            .sizeIn(minHeight = 72.dp)
            .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 12.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = if (capitalizeTitle) {
                    title.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                } else {
                    title
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Start, fontWeight = FontWeight.Bold, lineHeight = 20.sp
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    textAlign = TextAlign.Start,
                    color = ColorGrayScale500,
                ),
            )
        }
        Image(
            painter = painterResource(endIcon),
            contentDescription = "",
            modifier = Modifier.padding(start = 12.dp),
            alignment = Alignment.Center
        )
    }
}

@Preview
@Composable
private fun PreviewInfoField() = EduidAppAndroidTheme {
    InfoField(
        title = "Vetinari", subtitle = "Lord", label = "Full Name"
    )
}