package nl.eduid.ui

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import nl.eduid.screens.personalinfo.PersonalInfo
import nl.eduid.ui.theme.BlueButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGrayScale
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import nl.eduid.ui.theme.BlueText
import nl.eduid.ui.theme.SplashScreenBackgroundColor
import nl.eduid.ui.theme.TextBlack
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InfoTab(
    header: String = "",
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    institutionInfo: PersonalInfo.Companion.InstitutionAccount? = null,
    @DrawableRes endIcon: Int = 0,
    @DrawableRes startIcon: Int = 0,
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
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(width = (if (enabled) 3.dp else 1.dp), color = (if (enabled) BlueButton else TextGrayScale))
            .sizeIn(minHeight = 72.dp)
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 12.dp)
            .clickable {
                if (institutionInfo != null) {
                    isOpen.value = !isOpen.value
                } else {
                    onClick.invoke()
                }
            }
            .animateContentSize()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(0.85f),
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                modifier = Modifier.fillMaxWidth(0.85f),
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    textAlign = TextAlign.Start,
                    color = TextGrayScale,
                ),
            )
            if (isOpen.value && institutionInfo != null) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Verified by ${institutionInfo.institution} on ${institutionInfo.createdStamp.getDateTime()}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Start,
                        color = BlueText,
                    ),
                )
                Spacer(Modifier.height(12.dp))
                Divider(color = TextBlack, thickness = 1.dp)
                Spacer(Modifier.height(12.dp))

                Column(
                    Modifier
                        .fillMaxWidth()
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
                            text = "Institution",
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
                            text = institutionInfo.expiryStamp.getDateTime(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Start,
                                color = BlueText,
                            ),
                        )
                    }
                }
            }
        }
        Image(
            painter = painterResource(endIcon),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth(0.15f)
                .align(Alignment.TopEnd)
                .padding(top = 12.dp)
        )
    }
    Spacer(Modifier.height(16.dp))
}

@SuppressLint("SimpleDateFormat")
private fun Long.getDateTime(): String {
    return try {
        val sdf = SimpleDateFormat("EEEE MMM dd, yyyy")
        val netDate = Date(this)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}

@Preview
@Composable
private fun PreviewInfoTab() {
    EduidAppAndroidTheme {
        InfoTab(
            title = "OK",
            subtitle = "OK",
            onClick = { },
            enabled = true,
            endIcon = R.drawable.shield_tick_blue,
        )
    }
}