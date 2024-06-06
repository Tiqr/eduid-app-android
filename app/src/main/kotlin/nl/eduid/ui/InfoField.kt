package nl.eduid.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.eduid.R
import nl.eduid.screens.personalinfo.PersonalInfo
import nl.eduid.ui.theme.BlueButton
import nl.eduid.ui.theme.ColorScale_Gray_500
import nl.eduid.ui.theme.ColorSupport_Blue_100
import nl.eduid.ui.theme.EduidAppAndroidTheme
import java.util.Locale

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
                    color = ColorScale_Gray_500,
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
) = ListItem(colors = ListItemDefaults.colors(
    containerColor = MaterialTheme.colorScheme.surface,
    headlineColor = MaterialTheme.colorScheme.onSurface,
    supportingColor = MaterialTheme.colorScheme.onSurfaceVariant,
    trailingIconColor = MaterialTheme.colorScheme.onSurface
), headlineContent = {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
    )
}, supportingContent = { Text(text = subtitle) }, trailingContent = {
    Icon(
        painter = painterResource(id = R.drawable.edit_icon),
        contentDescription = "",
    )
}, modifier = modifier
    .fillMaxWidth()
    .border(
        color = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(6.dp),
        width = 2.dp
    )
)

@Composable
fun VerifiedInfoField(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            trailingIconColor = MaterialTheme.colorScheme.onSurface
        ),
        leadingContent = {
            Image(
                painter = painterResource(id = R.drawable.shield_tick_blue), contentDescription = ""
            )
        },
        headlineContent = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface
                ),
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        },
        trailingContent = {
            Icon(
                painter = painterResource(R.drawable.homepage_info_icon),
                tint = MaterialTheme.colorScheme.onSecondary,
                contentDescription = "",
            )
        },
        modifier = modifier
            .border(
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(6.dp),
                width = 2.dp
            )
    )
}

@Composable
fun ExpandableVerifiedInfoField(
    title: String,
    subtitle: String,
    confirmedByInstitution: PersonalInfo.InstitutionAccount,
    modifier: Modifier = Modifier,
    openVerifiedInformation: (String) -> Unit = {},
    expandedPreview: Boolean = false,
) {
    var isExpanded by remember { mutableStateOf(expandedPreview) }
    val containerColor = if (isExpanded) {
        ColorSupport_Blue_100
    } else {
        MaterialTheme.colorScheme.surface
    }
    ListItem(colors = ListItemDefaults.colors(
        containerColor = containerColor,
        trailingIconColor = MaterialTheme.colorScheme.onSurface
    ),
        leadingContent = {
            Image(
                painter = painterResource(id = R.drawable.shield_tick_blue), contentDescription = ""
            )
        },
        headlineContent = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface
                ),
            )
        },
        supportingContent = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = subtitle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                if (isExpanded) {
                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = stringResource(
                            id = R.string.Profile_VerifiedBy_COPY,
                            confirmedByInstitution.institution
                        ),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    )
                    VerifiedRowInfo(
                        R.string.Profile_VerifiedOnNoPlaceholder_COPY,
                        confirmedByInstitution.createdStamp.getDateString()
                    )
                    VerifiedRowInfo(
                        R.string.Profile_VerifiedValidUntil_COPY,
                        confirmedByInstitution.expiryStamp.getDateString()
                    )
                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSurface)
                    TextButton(
                        onClick = { openVerifiedInformation(confirmedByInstitution.linkedAccountJson) },
                        shape = RoundedCornerShape(CornerSize(6.dp)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.Profile_ManageYourVerifiedInformation_COPY),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal,
                                textDecoration = TextDecoration.Underline
                            ),
                        )
                    }
                }
            }
        },
        trailingContent = {
            if (isExpanded) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowUp,
                    contentDescription = "",
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = "",
                )
            }
        },
        modifier = modifier
            .border(
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(6.dp),
                width = 2.dp
            )
            .clickable {
                isExpanded = !isExpanded
            })
}

@Composable
fun VerifiedRowInfo(prefixId: Int, info: String) = Row {
    Text(text = stringResource(prefixId))
    Text(
        text = info,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview_InfoFields() = EduidAppAndroidTheme {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        InfoField(
            title = "Vetinari", subtitle = "First name"
        )
        VerifiedInfoField(
            title = "Vetinari", subtitle = "Verified family name",
        )
        ExpandableVerifiedInfoField(
            title = "Vetinari", subtitle = "First Name",
            confirmedByInstitution = PersonalInfo.generateInstitutionAccountList()[0],
        )

        ExpandableVerifiedInfoField(
            title = "Vetinari", subtitle = "Verified family name",
            confirmedByInstitution = PersonalInfo.generateInstitutionAccountList()[0],
            expandedPreview = true
        )
    }
}