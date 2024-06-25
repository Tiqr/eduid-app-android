package nl.eduid.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
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
import coil.compose.AsyncImage
import nl.eduid.R
import nl.eduid.screens.dataactivity.ScopeAccessGrant
import nl.eduid.screens.dataactivity.ServiceProvider
import nl.eduid.ui.theme.BlueButton
import nl.eduid.ui.theme.ColorAlertRed
import nl.eduid.ui.theme.ColorScale_Gray_500
import nl.eduid.ui.theme.ColorScale_Gray_Black
import nl.eduid.ui.theme.ColorSupport_Blue_100
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.InfoTabDarkFill
import java.util.Locale

@Composable
fun LoginInfoCard(
    title: String,
    modifier: Modifier = Modifier,
    serviceProviderInfo: ServiceProvider? = null,
    isExpanded: Boolean = false,
    onDeleteServiceClicked: () -> Unit = { },
    onRevokeTokenClicked: (scope: ScopeAccessGrant) -> Unit = { },
) {
    var isExpanded by remember { mutableStateOf(isExpanded) }
    val containerColor = if (isExpanded) ColorSupport_Blue_100 else MaterialTheme.colorScheme.surface
    val titleColor = if (isExpanded) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant

    ListItem(
        colors =
            ListItemDefaults.colors(
                containerColor = containerColor,
                headlineColor = titleColor,
                trailingIconColor = titleColor,
            ),
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                serviceProviderInfo?.providerLogoUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                } ?: Image(painterResource(id = R.drawable.ic_placeholder_image), contentDescription = "")
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    softWrap = true,
                    maxLines = 3,
                    modifier = Modifier.weight(1.0f),
                )
                if (serviceProviderInfo?.hasDataAccess == true) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_key_data_activity_access),
                        contentDescription = "",
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = "",
                )
            }
        },
        supportingContent = {
            if (isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(stringResource(R.string.DataActivity_Details_Login_COPY), style = MaterialTheme.typography.labelSmall)
                    serviceProviderInfo?.firstLoginStamp?.let {
                        InfoRow(stringResource(R.string.DataActivity_Details_FirstLogin_COPY), it.getDateString())
                    }
                    serviceProviderInfo?.uniqueId?.let {
                        InfoRow(stringResource(R.string.DataActivity_Details_UniqueEduID_COPY), it)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                onDeleteServiceClicked()
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = TextDecoration.Underline,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                            text = stringResource(id = R.string.DataActivity_Details_Delete_COPY),
                        )
                    }
                    serviceProviderInfo?.availableTokens?.forEach { scopeGranted ->
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(stringResource(R.string.DataActivity_Details_Access_COPY), style = MaterialTheme.typography.labelSmall)
                        Text(scopeGranted.scopeDescription.orEmpty(), style = MaterialTheme.typography.labelSmall)
                        scopeGranted.grantedOn?.let {
                            InfoRow(stringResource(R.string.DataActivity_Details_Consent_COPY), it.formatStringDate())
                        }
                        scopeGranted.expireAt?.let {
                            InfoRow(stringResource(R.string.DataActivity_Details_Expires_COPY), it.formatStringDate())
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    scopeGranted.token?.let {
                                        onRevokeTokenClicked(scopeGranted)
                                    }
                                },
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "",
                                modifier = Modifier.size(24.dp),
                            )
                            Text(
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = TextDecoration.Underline,
                                    color = MaterialTheme.colorScheme.primary,
                                ),
                                text = stringResource(id = R.string.DataActivity_Details_Revoke_COPY),
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        },
        modifier =
            modifier
                .border(
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(6.dp),
                    width = 2.dp,
                ).clickable {
                    isExpanded = !isExpanded
                },
    )
}

@Composable
fun LoginInfoCardOld(
    title: String,
    subtitle: String,
    serviceProviderInfo: ServiceProvider? = null,
    isExpanded: Boolean = false,
    onDeleteButtonClicked: (id: String) -> Unit = { },
    startIconLargeUrl: String = "",
) {
    val isOpen = remember { mutableStateOf(isExpanded) }
    Spacer(Modifier.height(6.dp))
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 3.dp,
                    color = BlueButton,
                ).sizeIn(minHeight = 72.dp)
                .fillMaxWidth()
                .clickable {
                    if (serviceProviderInfo != null) {
                        isOpen.value = !isOpen.value
                    }
                }.background(InfoTabDarkFill)
                .animateContentSize(),
    ) {
        ConstraintLayout(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 12.dp),
        ) {
            val (startImage, titleArea, endImage, expandedArea) = createRefs()
            Box(
                modifier =
                    Modifier
                        .constrainAs(startImage) {
                            top.linkTo(titleArea.top)
                            bottom.linkTo(titleArea.bottom)
                            start.linkTo(parent.start)
                        }.height(48.dp),
            ) {
                if (startIconLargeUrl.isNotBlank()) {
                    AsyncImage(
                        model = startIconLargeUrl,
                        contentDescription = null,
                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .padding(end = 12.dp)
                                .heightIn(max = 48.dp)
                                .widthIn(max = 48.dp),
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.Start,
                modifier =
                    Modifier.constrainAs(titleArea) {
                        top.linkTo(parent.top)
                        start.linkTo(startImage.end)
                        end.linkTo(endImage.start)
                        width = Dimension.fillToConstraints
                    },
            ) {
                Text(
                    text =
                        title.replaceFirstChar {
                            if (it.isLowerCase()) {
                                it.titlecase(
                                    Locale.getDefault(),
                                )
                            } else {
                                it.toString()
                            }
                        },
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 20.sp,
                        ),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            textAlign = TextAlign.Start,
                            color = ColorScale_Gray_500,
                        ),
                )
            }
            if (isOpen.value && serviceProviderInfo != null) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier =
                        Modifier.constrainAs(expandedArea) {
                            top.linkTo(titleArea.bottom, margin = 24.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                ) {
                    ServiceProviderBlock(serviceProviderInfo, onDeleteButtonClicked)
                }
            }
            Image(
                painter = painterResource(R.drawable.chevron_down),
                contentDescription = "",
                modifier =
                    Modifier
                        .constrainAs(endImage) {
                            top.linkTo(titleArea.top)
                            bottom.linkTo(titleArea.bottom)
                            end.linkTo(parent.end)
                        }.padding(start = 12.dp),
            )
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun ServiceProviderBlock(serviceProviderInfo: ServiceProvider, onDeleteButtonClicked: (id: String) -> Unit) = Column(
    Modifier.fillMaxWidth(),
) {
    InfoRowOld(label = stringResource(R.string.DataActivity_Details_Login_COPY))
    serviceProviderInfo.firstLoginStamp?.let {
        InfoRowOld(
            label = stringResource(R.string.DataActivity_Details_FirstLogin_COPY),
            value = it.getDateString(),
        )
    }
    serviceProviderInfo.uniqueId?.let {
        InfoRowOld(
            label = stringResource(R.string.DataActivity_Details_UniqueEduID_COPY),
            value = it,
        )
    }
    Button(
        shape = RoundedCornerShape(CornerSize(6.dp)),
        onClick = {
            serviceProviderInfo.uniqueId?.let {
                onDeleteButtonClicked(it)
            }
        },
        border = BorderStroke(1.dp, Color.Red),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorAlertRed),
        modifier =
            Modifier
                .sizeIn(minHeight = 48.dp)
                .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.DataActivity_Details_Delete_COPY),
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    color = ColorAlertRed,
                    fontWeight = FontWeight.SemiBold,
                ),
        )
    }
    Spacer(Modifier.height(24.dp))
    Text(
        text = stringResource(R.string.DataActivity_Details_DeleteDisclaimer_COPY),
        style =
            MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Start,
                color = ColorScale_Gray_Black,
            ),
    )
    Spacer(Modifier.height(32.dp))
}

@Preview
@Composable
private fun PreviewLoginInfoCard() = EduidAppAndroidTheme {
    Column {
        LoginInfoCard(
            title = "My eduid",
            serviceProviderInfo =
                ServiceProvider(
                    providerName = "Provider name",
                    createdStamp = 0L,
                    firstLoginStamp = 0L,
                    uniqueId = "15a6dObf-fOfd-466d-a3b1-ff19df2dcc3e",
                    serviceProviderEntityId = "service provider id",
                    providerLogoUrl = "https://static.surfconext.nl/media/sp/eduid.png",
                ),
            isExpanded = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
        )
        LoginInfoCard(
            title = "eduID invite with a very long name",
            serviceProviderInfo =
                ServiceProvider(
                    providerName = "Provider name",
                    createdStamp = 0L,
                    firstLoginStamp = 0L,
                    uniqueId = "15a6dObf-fOfd-466d-a3b1-ff19df2dcc3e",
                    serviceProviderEntityId = "service provider id",
                    availableTokens = listOf(
                        ScopeAccessGrant(
                            clientId = "test.eduid.nl",
                            forProviderName = "eduid mobile app",
                            scopeDescription = "Allow this app to read and update your eduID data",
                            grantedOn = "2024-06-13T14:25:57.054+00:00",
                            expireAt = "2024-06-13T14:25:57.054+00:00",
                        ),
                    ),
                ),
            isExpanded = true,
            modifier = Modifier.fillMaxWidth(),
        )
        LoginInfoCardOld(
            title = "My eduid",
            subtitle = "on Date",
            serviceProviderInfo =
                ServiceProvider(
                    providerName = "Provider name",
                    createdStamp = 0L,
                    firstLoginStamp = 0L,
                    uniqueId = "15a6dObf-fOfd-466d-a3b1-ff19df2dcc3e",
                    serviceProviderEntityId = "service provider id",
                    providerLogoUrl = "https://static.surfconext.nl/media/sp/eduid.png",
                ),
            isExpanded = true,
        )
    }
}