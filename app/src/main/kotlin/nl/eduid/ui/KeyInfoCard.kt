package nl.eduid.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import nl.eduid.R
import nl.eduid.screens.twofactorkey.IdentityData
import nl.eduid.ui.theme.BlueButton
import nl.eduid.ui.theme.BlueText
import nl.eduid.ui.theme.ButtonRed
import nl.eduid.ui.theme.InfoTabDarkFill
import nl.eduid.ui.theme.TextGrayScale
import java.util.Locale

@Composable
fun KeyInfoCard(
    title: String,
    subtitle: String,
    keyData: IdentityData? = null,
    onDeleteButtonClicked: (id: String) -> Unit = { },
    onChangeBiometric: (IdentityData, Boolean) -> Unit = { _, _ -> },
    onExpand: (IdentityData?) -> Unit = { _ -> }
) {
    Spacer(Modifier.height(6.dp))
    Box(modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .border(
            width = 3.dp, color = BlueButton
        )
        .sizeIn(minHeight = 72.dp)
        .fillMaxWidth()
        .clickable {
            onExpand(keyData)
        }
        .background(InfoTabDarkFill)
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
            if (keyData != null && keyData.isExpanded) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.constrainAs(expandedArea) {
                        top.linkTo(titleArea.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }) {
                    TwoFABlock(
                        keyInfo = keyData,
                        onDeleteButtonClicked = onDeleteButtonClicked,
                        onChangeBiometric = onChangeBiometric
                    )
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
private fun TwoFABlock(
    keyInfo: IdentityData,
    onDeleteButtonClicked: (id: String) -> Unit,
    onChangeBiometric: (IdentityData, Boolean) -> Unit = { _, _ -> }
) = Column(
    Modifier.fillMaxWidth()
) {
    InfoRow(
        label = stringResource(R.string.two_fa_account),
        value = keyInfo.account,
    )
    InfoRow(
        label = stringResource(R.string.two_fa_unieque_keyid),
        value = keyInfo.uniqueKey,
    )

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
        Switch(
            checked = keyInfo.biometricFlag,
            onCheckedChange = { onChangeBiometric(keyInfo, it) }
        )
    }
    Spacer(Modifier.height(24.dp))
    Button(
        shape = RoundedCornerShape(CornerSize(6.dp)),
        onClick = { onDeleteButtonClicked(keyInfo.uniqueKey) },
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

