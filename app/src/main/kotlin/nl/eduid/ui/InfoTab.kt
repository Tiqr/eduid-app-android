package nl.eduid.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import nl.eduid.R
import nl.eduid.screens.dataactivity.ServiceProvider
import nl.eduid.screens.personalinfo.PersonalInfo
import nl.eduid.screens.twofactorkey.IdentityData
import nl.eduid.ui.theme.BlueButton
import nl.eduid.ui.theme.BlueText
import nl.eduid.ui.theme.ButtonRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.InfoTabDarkFill
import nl.eduid.ui.theme.TextBlack
import nl.eduid.ui.theme.TextGrayScale
import java.util.Locale

@Composable
fun InfoField(
    title: String,
    subtitle: String,
    onClick: () -> Unit = {},
    @DrawableRes endIcon: Int = 0,
) = Row(modifier = Modifier
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
    Column {
        Text(
            text = title.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
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
        modifier = Modifier.padding(start = 12.dp)
    )
}

@Composable
fun InfoTab(
    header: String = "",
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    twoFactorData: IdentityData? = null,
    onDeleteButtonClicked: (id: String) -> Unit = { },
    institutionInfo: PersonalInfo.InstitutionAccount? = null,
    serviceProviderInfo: ServiceProvider? = null,
    startIconLargeUrl: String = "",
    @DrawableRes endIcon: Int = 0,
) {
    val isOpen = remember { mutableStateOf(false) }
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
    Box(modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .border(
            width = 3.dp, color = BlueButton
        )
        .sizeIn(minHeight = 72.dp)
        .fillMaxWidth()
        .clickable {
            if (institutionInfo != null || serviceProviderInfo != null || twoFactorData != null) {
                isOpen.value = !isOpen.value
            } else {
                onClick.invoke()
            }
        }
        .background(if (serviceProviderInfo != null || twoFactorData != null) InfoTabDarkFill else Color.Transparent)
        .animateContentSize()) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 12.dp)
        ) {
            val (startImage, titleArea, endImage, expandedArea) = createRefs()
            Box(modifier = Modifier
                .constrainAs(startImage) {
                    top.linkTo(titleArea.top)
                    bottom.linkTo(titleArea.bottom)
                    start.linkTo(parent.start)
                }
                .height(48.dp)) {
                if (startIconLargeUrl.isNotBlank()) {
                    AsyncImage(
                        model = startIconLargeUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(end = 12.dp)
                            .heightIn(max = 48.dp)
                            .widthIn(max = 48.dp),
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.constrainAs(titleArea) {
                    top.linkTo(parent.top)
                    start.linkTo(startImage.end)
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
                        color = TextGrayScale,
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
                    InstitutionInfoBlock(institutionInfo, onDeleteButtonClicked)
                }
            }
            if (isOpen.value && serviceProviderInfo != null) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.constrainAs(expandedArea) {
                        top.linkTo(titleArea.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    content = serviceProviderBlock(serviceProviderInfo, onDeleteButtonClicked)
                )
            }
            if (isOpen.value && twoFactorData != null) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.constrainAs(expandedArea) {
                        top.linkTo(titleArea.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    content = twoFaBlock(twoFactorData, onDeleteButtonClicked)
                )
            }
            Image(painter = painterResource(endIcon),
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
) {
    Text(
        text = "Verified by ${institutionInfo.institution} on ${institutionInfo.createdStamp.getDateString()}",
        style = MaterialTheme.typography.bodyMedium.copy(
            textAlign = TextAlign.Start,
            color = BlueText,
        ),
    )
    Spacer(Modifier.height(12.dp))
    Divider(color = TextBlack, thickness = 1.dp)
    Spacer(Modifier.height(12.dp))

    Column(
        Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Institution",
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
            Text(
                modifier = Modifier.weight(1f),
                text = institutionInfo.institution,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
        }
        Spacer(Modifier.height(12.dp))
        Divider(color = TextBlack, thickness = 1.dp)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Affiliation(s)",
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
            Text(
                modifier = Modifier.weight(1f),
                text = institutionInfo.affiliationString,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
        }
        Spacer(Modifier.height(12.dp))
        Divider(color = TextBlack, thickness = 1.dp)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Link Expires",
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
            Text(
                modifier = Modifier.weight(1f),
                text = institutionInfo.expiryStamp.getDateString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
        }
        Spacer(Modifier.height(12.dp))
        Divider(color = TextBlack, thickness = 1.dp)
        Spacer(Modifier.height(12.dp))
    }
    Button(
        shape = RoundedCornerShape(CornerSize(6.dp)),
        onClick = { onDeleteButtonClicked(institutionInfo.id) },
        border = BorderStroke(1.dp, Color.Red),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonRed),
        modifier = Modifier
            .sizeIn(minHeight = 48.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.infotab_remove_connection),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = ButtonRed, fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun serviceProviderBlock(
    serviceProviderInfo: ServiceProvider,
    onDeleteButtonClicked: (id: String) -> Unit,
): @Composable() (ColumnScope.() -> Unit) = {
    Text(
        text = stringResource(R.string.infotab_login_details),
        style = MaterialTheme.typography.bodyMedium.copy(
            textAlign = TextAlign.Start,
            color = BlueText,
        ),
    )
    Spacer(Modifier.height(12.dp))
    Divider(color = TextBlack, thickness = 1.dp)
    Spacer(Modifier.height(12.dp))
    Column(
        Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "First Login",
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
            Text(
                modifier = Modifier.weight(1f),
                text = serviceProviderInfo.firstLoginStamp.getDateString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
        }
        Spacer(Modifier.height(12.dp))
        Divider(color = TextBlack, thickness = 1.dp)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Unique eduID",
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
            Text(
                modifier = Modifier.weight(1f),
                text = serviceProviderInfo.uniqueId,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
        }
        Spacer(Modifier.height(12.dp))
        Divider(color = TextBlack, thickness = 1.dp)
        Spacer(Modifier.height(12.dp))
        Button(
            shape = RoundedCornerShape(CornerSize(6.dp)),
            onClick = { onDeleteButtonClicked(serviceProviderInfo.uniqueId) },
            border = BorderStroke(1.dp, Color.Red),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonRed),
            modifier = Modifier
                .sizeIn(minHeight = 48.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.infotab_delete_login_details),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = ButtonRed, fontWeight = FontWeight.SemiBold
                )
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.data_info_delete_disclaimer),
            style = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Start,
                color = TextBlack,
            ),
        )
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun twoFaBlock(
    twoFactorData: IdentityData,
    onDeleteButtonClicked: (id: String) -> Unit,
): @Composable() (ColumnScope.() -> Unit) = {
    val biometricsCheckState = remember { mutableStateOf(true) }
    Spacer(Modifier.height(12.dp))
    Column(
        Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Account",
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
            Text(
                modifier = Modifier.weight(1f),
                text = twoFactorData.account,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
        }
        Spacer(Modifier.height(12.dp))
        Divider(color = TextBlack, thickness = 1.dp)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Unique KeyID",
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
            Text(
                modifier = Modifier.weight(1f),
                text = twoFactorData.uniqueKey,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
        }
        Spacer(Modifier.height(12.dp))
        Divider(color = TextBlack, thickness = 1.dp)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.two_fa_use_biometric),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    color = BlueText,
                ),
            )
            Switch(checked = biometricsCheckState.value,
                onCheckedChange = { biometricsCheckState.value = it })
        }
        Spacer(Modifier.height(24.dp))
        Button(
            shape = RoundedCornerShape(CornerSize(6.dp)),
            onClick = { onDeleteButtonClicked(twoFactorData.uniqueKey) },
            border = BorderStroke(1.dp, Color.Red),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonRed),
            modifier = Modifier
                .sizeIn(minHeight = 48.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.two_fa_delete),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = ButtonRed, fontWeight = FontWeight.SemiBold
                )
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}


@Preview
@Composable
private fun PreviewInfoTab() {
    EduidAppAndroidTheme {
        InfoTab(
            header = "Header",
            title = "OK a very long long long long long long long long long long",
            subtitle = "OK long long long long long long long long long long long long",
            onClick = { },
            endIcon = R.drawable.shield_tick_blue,
            institutionInfo = PersonalInfo.InstitutionAccount(
                role = "Long string here",
                roleProvider = "Long string here",
                institution = "Long string here",
                affiliationString = "Long string here",
                createdStamp = 1231321321321,
                expiryStamp = 12313213131313,
                id = "123",
            ),
            startIconLargeUrl = "https://static.surfconext.nl/media/sp/eduid.png"
        )
    }
}

@Preview
@Composable
private fun PreviewInfoField() = EduidAppAndroidTheme {
    InfoField(
        title = "Vetinari", subtitle = "Lord"
    )
}
