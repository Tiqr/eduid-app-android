package nl.eduid.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import nl.eduid.R
import nl.eduid.screens.dataactivity.ScopeAccessGrant
import nl.eduid.screens.dataactivity.ServiceProvider
import nl.eduid.ui.theme.ColorSupport_Blue_100
import nl.eduid.ui.theme.EduidAppAndroidTheme

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
    val borderColor = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
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
                        InfoRow(
                            stringResource(R.string.DataActivity_Details_FirstLogin_COPY),
                            it.getShortDateString()
                        )
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
                            painter = painterResource(id = R.drawable.ic_delete_icon),
                            contentDescription = null,
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
                                painter = painterResource(id = R.drawable.ic_delete_icon),
                                contentDescription = null,
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
                color = borderColor,
                shape = RoundedCornerShape(6.dp),
                width = 2.dp,
            )
            .clickable {
                isExpanded = !isExpanded
            },
    )
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
    }
}