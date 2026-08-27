package nl.eduid.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.theme.ColorAlertRed
import nl.eduid.ui.theme.ColorMain_Green_400
import nl.eduid.ui.theme.ColorScale_Gray_Black
import nl.eduid.ui.theme.ColorSupport_Blue_100
import nl.eduid.ui.theme.EduidAppAndroidTheme

/**
 * Confirmation layout for destructive actions: title, a "can't undo" warning, a description and
 * cancel/confirm buttons, where the confirm button is red.
 */
@Composable
fun DestructiveConfirmationContent(
    title: String,
    warning: String,
    description: String,
    cancelLabel: String,
    confirmLabel: String,
    modifier: Modifier = Modifier,
    inProgress: Boolean = false,
    padding: PaddingValues = PaddingValues(),
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
) = Column(
    modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .navigationBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(color = ColorMain_Green_400, textAlign = TextAlign.Start),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = ColorSupport_Blue_100)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.warning_icon_red),
                contentDescription = null,
            )
            Text(
                text = warning,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(18.dp))
        if (inProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(18.dp))
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge.copy(color = ColorScale_Gray_Black, textAlign = TextAlign.Start),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    Spacer(Modifier.height(18.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth(),
    ) {
        SecondaryButton(
            enabled = !inProgress,
            text = cancelLabel,
            modifier = Modifier.widthIn(min = 140.dp),
            onClick = onCancel,
        )
        PrimaryButton(
            enabled = !inProgress,
            text = confirmLabel,
            modifier = Modifier.widthIn(min = 140.dp),
            onClick = onConfirm,
            buttonBackgroundColor = ColorAlertRed,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_DestructiveConfirmationContent() = EduidAppAndroidTheme {
    DestructiveConfirmationContent(
        title = "Delete passkey",
        warning = "You can't undo this action",
        description = "Are you sure you want to delete the passkey \"My laptop\"?",
        cancelLabel = "No, back",
        confirmLabel = "Yes, delete",
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview_DestructiveConfirmationContent_InProgress() = EduidAppAndroidTheme {
    DestructiveConfirmationContent(
        title = "Delete passkey",
        warning = "You can't undo this action",
        description = "Are you sure you want to delete the passkey \"My laptop\"?",
        cancelLabel = "No, back",
        confirmLabel = "Yes, delete",
        inProgress = true,
    )
}
