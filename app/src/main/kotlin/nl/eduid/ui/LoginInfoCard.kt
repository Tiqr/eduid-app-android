package nl.eduid.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
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
import nl.eduid.ui.theme.BlueButton
import nl.eduid.ui.theme.ColorAlertRed
import nl.eduid.ui.theme.ColorScale_Gray_500
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.InfoTabDarkFill
import nl.eduid.ui.theme.ColorScale_Gray_Black
import java.util.Locale

@Composable
fun LoginInfoCard(
    title: String,
    subtitle: String,
    serviceProviderInfo: ServiceProvider? = null,
    isExpanded: Boolean = false,
    onDeleteButtonClicked: (id: String) -> Unit = { },
    startIconLargeUrl: String = "",
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
            if (serviceProviderInfo != null) {
                isOpen.value = !isOpen.value
            }
        }
        .background(InfoTabDarkFill)
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

            Column(horizontalAlignment = Alignment.Start,
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
                        color = ColorScale_Gray_500,
                    ),
                )
            }
            if (isOpen.value && serviceProviderInfo != null) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.constrainAs(expandedArea) {
                        top.linkTo(titleArea.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                ) {
                    ServiceProviderBlock(serviceProviderInfo, onDeleteButtonClicked)
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
private fun ServiceProviderBlock(
    serviceProviderInfo: ServiceProvider,
    onDeleteButtonClicked: (id: String) -> Unit,
) = Column(
    Modifier.fillMaxWidth()
) {
    InfoRow(label = stringResource(R.string.DataActivity_Details_Login_COPY))
    serviceProviderInfo.firstLoginStamp?.let {
        InfoRow(
            label = stringResource(R.string.DataActivity_Details_FirstLogin_COPY),
            value = it.getDateString()
        )
    }
    serviceProviderInfo.uniqueId?.let {
        InfoRow(
            label = stringResource(R.string.DataActivity_Details_UniqueEduID_COPY),
            value = it
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
        modifier = Modifier
            .sizeIn(minHeight = 48.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.DataActivity_Details_Delete_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = ColorAlertRed, fontWeight = FontWeight.SemiBold
            )
        )
    }
    Spacer(Modifier.height(24.dp))
    Text(
        text = stringResource(R.string.DataActivity_Details_DeleteDisclaimer_COPY),
        style = MaterialTheme.typography.bodySmall.copy(
            textAlign = TextAlign.Start,
            color = ColorScale_Gray_Black,
        ),
    )
    Spacer(Modifier.height(32.dp))
}


@Preview
@Composable
private fun PreviewLoginInfoCard() = EduidAppAndroidTheme {
    LoginInfoCard(
        title = "My eduid", subtitle = "on Date", serviceProviderInfo = ServiceProvider(
            providerName = "Provider name",
            createdStamp = 0L,
            firstLoginStamp = 0L,
            uniqueId = "uniqueId",
            serviceProviderEntityId = "service provider id",
            providerLogoUrl = ""
        ), isExpanded = true
    )
}