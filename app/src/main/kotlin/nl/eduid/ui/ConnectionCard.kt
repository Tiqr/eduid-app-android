package nl.eduid.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import nl.eduid.R
import nl.eduid.screens.personalinfo.PersonalInfo
import nl.eduid.screens.personalinfo.PersonalInfo.Companion.generateInstitutionAccountList
import nl.eduid.ui.theme.BlueButton
import nl.eduid.ui.theme.ColorAlertRed
import nl.eduid.ui.theme.ColorScale_Gray_500
import nl.eduid.ui.theme.ColorSupport_Blue_100
import nl.eduid.ui.theme.EduidAppAndroidTheme
import java.util.Locale

@Composable
fun ConnectionCard(
    title: String,
    confirmedByInstitution: PersonalInfo.InstitutionAccount,
    modifier: Modifier = Modifier,
    expandedPreview: Boolean = false,
    openVerifiedInformation: () -> Unit = {},
) {
    var isExpanded by remember { mutableStateOf(expandedPreview) }
    val containerColor = if (isExpanded) {
        ColorSupport_Blue_100
    } else {
        MaterialTheme.colorScheme.surface
    }
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
            trailingIconColor = MaterialTheme.colorScheme.onSurface
        ),
        leadingContent = {},
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
                    text = stringResource(
                        id = R.string.YourVerifiedInformation_AtInstitution_COPY,
                        confirmedByInstitution.institution
                    ),
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
                        R.string.Profile_LinkedAccountCreatedAt_COPY,
                        confirmedByInstitution.createdStamp.getShortDateString()
                    )
                    VerifiedRowInfo(
                        R.string.Profile_LinkedAccountValidUntil_COPY,
                        confirmedByInstitution.expiryStamp.getShortDateString()
                    )
                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSurface)
                    TextButton(
                        onClick = { openVerifiedInformation() },
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
            }
    )
}

@Composable
fun ConnectionCardOld(
    title: String,
    subtitle: String,
    institutionInfo: PersonalInfo.InstitutionAccount? = null,
    isExpanded: Boolean = false,
    onRemoveConnection: (id: String) -> Unit = { },
) {
    val isOpen = remember { mutableStateOf(isExpanded) }
    Spacer(Modifier.height(6.dp))
    Box(modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .border(
            width = 3.dp, color = BlueButton
        )
        .sizeIn(minHeight = 72.dp)
        .fillMaxWidth()
        .clickable {
            if (institutionInfo != null) {
                isOpen.value = !isOpen.value
            }
        }
        .animateContentSize()) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 12.dp)
        ) {
            val (titleArea, endImage, expandedArea) = createRefs()

            Column(horizontalAlignment = Alignment.Start,
                modifier = Modifier.constrainAs(titleArea) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(endImage.start)
                    width = Dimension.fillToConstraints
                }) {
                Text(
                    text = title.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
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
                        color = ColorScale_Gray_500,
                    ),
                )
            }
            if (isOpen.value && institutionInfo != null) {
                Column(horizontalAlignment = Alignment.Start,
                    modifier = Modifier.constrainAs(expandedArea) {
                        top.linkTo(titleArea.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }) {
                    InstitutionInfoBlock(institutionInfo, onRemoveConnection)
                }
            }
            Image(painter = painterResource(R.drawable.chevron_down),
                contentDescription = "",
                modifier = Modifier
                    .constrainAs(endImage) {
                        top.linkTo(titleArea.top)
                        bottom.linkTo(titleArea.bottom)
                        end.linkTo(parent.end)
                    }
                    .padding(start = 12.dp))
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun InstitutionInfoBlock(
    institutionInfo: PersonalInfo.InstitutionAccount,
    onDeleteButtonClicked: (id: String) -> Unit,
) = Column(
    Modifier.fillMaxWidth()
) {
    InfoRowOld(
        label = stringResource(R.string.Profile_VerifiedBy_COPY, institutionInfo.institution)
                + stringResource(
            R.string.Profile_VerifiedOn_COPY, institutionInfo.createdStamp.getShortDateString()
        )
    )
    InfoRowOld(
        label = stringResource(R.string.Profile_Institution_COPY),
        value = institutionInfo.institution
    )
    if (institutionInfo.affiliationString != null) {
        InfoRowOld(
            label = stringResource(R.string.Profile_Affiliations_COPY),
            value = institutionInfo.affiliationString
        )
    }
    InfoRowOld(
        label = stringResource(R.string.Profile_Expires_COPY),
        value = institutionInfo.expiryStamp.getShortDateString()
    )
    Button(
        shape = RoundedCornerShape(CornerSize(6.dp)),
        onClick = { onDeleteButtonClicked(institutionInfo.id) },
        border = BorderStroke(1.dp, Color.Red),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorAlertRed),
        modifier = Modifier
            .sizeIn(minHeight = 48.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.Institution_Delete_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = ColorAlertRed, fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Preview
@Composable
private fun Preview_ConnectionCard() = EduidAppAndroidTheme {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ConnectionCard(
            title = "Librarian",
            confirmedByInstitution = generateInstitutionAccountList()[0],
        )
        ConnectionCard(
            title = "Librarian",
            confirmedByInstitution = generateInstitutionAccountList()[0],
            expandedPreview = true,
        )

        ConnectionCardOld(
            title = "Librarian",
            subtitle = "Urangutan",
            institutionInfo = generateInstitutionAccountList()[0],
            isExpanded = true
        )

    }
}