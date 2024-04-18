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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGrayScale
import java.util.Locale

@Composable
fun InfoFieldOld(
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
                    color = TextGrayScale,
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
fun InfoFieldOld(
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
                    color = TextGrayScale,
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
    modifier: Modifier = Modifier,
) = ListItem(colors = ListItemDefaults.colors(trailingIconColor = BlueButton), headlineContent = {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth(),
    )
}, supportingContent = {
    Text(
        text = subtitle,
        modifier = Modifier.fillMaxWidth(),
    )
}, trailingContent = {
    Icon(
        imageVector = Icons.Outlined.Edit,
        contentDescription = "",
    )
}, modifier = modifier
)

@Composable
fun VerifiedInfoField(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    expandedPreview: Boolean = false,
    canExpand: Boolean = false,
) {
    var expanded by remember { mutableStateOf(expandedPreview) }
    ListItem(colors = ListItemDefaults.colors(trailingIconColor = BlueButton), leadingContent = {
        Image(
            painter = painterResource(id = R.drawable.shield_tick_blue), contentDescription = ""
        )
    }, headlineContent = {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
        )
    }, supportingContent = {
        Column {
            Text(
                text = subtitle,
                modifier = Modifier.fillMaxWidth(),
            )
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 2.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    },
        trailingContent = {
            if (canExpand) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = "",
                )
            }
        },
        modifier = modifier.clickable {
            expanded = !expanded
        }
    )
}

@Preview
@Composable
private fun Preview_OldInfoField() = EduidAppAndroidTheme {
    InfoFieldOld(
        title = "Vetinari", subtitle = "Lord", label = "Full Name"
    )
}

@Preview
@Composable
private fun Preview_InfoField() = EduidAppAndroidTheme {
    InfoField(
        title = "Vetinari", subtitle = "Lord"
    )
}

@Preview
@Composable
private fun Preview_VerifiedInfoField() = EduidAppAndroidTheme {
    VerifiedInfoField(
        title = "Vetinari", subtitle = "Lord", canExpand = true
    )
}


@Preview
@Composable
private fun Preview_VerifiedInfoField_Expanded() = EduidAppAndroidTheme {
    VerifiedInfoField(title = "Vetinari", subtitle = "Verified family name", expandedPreview = true)
}