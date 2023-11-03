package nl.eduid.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
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
import nl.eduid.R
import nl.eduid.screens.personalinfo.PersonalInfo
import nl.eduid.ui.theme.BlueButton
import nl.eduid.ui.theme.ButtonRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGrayScale
import java.util.Locale

@Composable
fun ConnectionCard(
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
    InfoRow(
        label = stringResource(R.string.Profile_VerifiedBy_COPY, institutionInfo.institution)
                + stringResource(R.string.Profile_VerifiedOn_COPY, institutionInfo.createdStamp.getDateString()
        )
    )
    InfoRow(
        label = stringResource(R.string.Profile_Institution_COPY),
        value = institutionInfo.institution
    )
    InfoRow(
        label = stringResource(R.string.Profile_Affiliations_COPY),
        value = institutionInfo.affiliationString
    )
    InfoRow(
        label = stringResource(R.string.Profile_Expires_COPY),
        value = institutionInfo.expiryStamp.getDateString()
    )
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
            text = stringResource(R.string.Institution_Delete_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = ButtonRed, fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Preview
@Composable
private fun PreviewConnectionCard() = EduidAppAndroidTheme {
    ConnectionCard(
        title = "Librarian",
        subtitle = "Urangutan",
        institutionInfo = PersonalInfo.InstitutionAccount(
            id = "id",
            role = "Librarian",
            roleProvider = "Library",
            institution = "Unseen University",
            affiliationString = "Librarian",
            createdStamp = 0L,
            expiryStamp = 0L
        ),
        isExpanded = true
    )
}