package nl.eduid.screens.pinsetup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.PinInputField
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun PinContent(
    pinCode: String,
    pinStep: PinStep,
    isPinInvalid: Boolean,
    title: String,
    description: String,
    label: String,
    onPinChange: (String, PinStep) -> Unit = { _, _ -> },
    onClick: () -> Unit = {},
    isProcessing: Boolean = false,
) = Column(
    modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceBetween
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )
        if (isProcessing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
        PinInputField(
            label = label,
            pinCode = pinCode,
            isPinInvalid = isPinInvalid,
            modifier = Modifier.fillMaxWidth(),
            onPinChange = { newValue -> onPinChange(newValue, pinStep) },
            submitPin = onClick
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
    PrimaryButton(
        text = stringResource(R.string.button_ok),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
            .padding(bottom = 24.dp),
    )
}

@Preview
@Composable
private fun Preview_PinContent() = EduidAppAndroidTheme {
    PinContent(
        pinCode = "1234",
        pinStep = PinStep.PinCreate,
        isPinInvalid = false,
        title = "Choose a unique PIN",
        description = "Enter the PIN:",
        label = "Please remember this PIN, it cannot be changed!",
    )
}