package nl.eduid.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.theme.EduidAppAndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinInputField(
    label: String?,
    pinCode: String,
    isPinInvalid: Boolean,
    modifier: Modifier = Modifier,
    onPinChange: (String) -> Unit = {},
    submitPin: () -> Unit = {},
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    if (!label.isNullOrEmpty()) {
        Text(
            text = if (isPinInvalid) "$label*" else label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
    Spacer(Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth(align = Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            for (i in 0 until PIN_MAX_LENGTH) {
                val code = if (pinCode.length - 1 >= i) {
                    pinCode[i].toString()
                } else {
                    ""
                }
                OutlinedTextField(
                    value = code,
                    onValueChange = {},
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
        val options = if (pinCode.length == PIN_MAX_LENGTH) {
            KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            )
        } else {
            KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            )
        }
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }
        OutlinedTextField(
            value = pinCode,
            onValueChange = onPinChange,
            singleLine = true,
            isError = isPinInvalid,
            keyboardOptions = options,
            keyboardActions = KeyboardActions(
                onNext = {
                    if (isPinInvalid.not()) {
                        focusManager.clearFocus()
                    }
                },
                onDone = {
                    focusManager.clearFocus()
                    submitPin.invoke()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .alpha(0f),
        )
    }

    // Supporting text for error message.
    if (isPinInvalid) {
        Text(
            text = stringResource(R.string.confirmpin_mismatch),
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(start = 8.dp, top = 4.dp)
        )
    }
}

const val PIN_MAX_LENGTH = 5

@ExperimentalMaterial3Api
@Preview
@Composable
private fun PinInputField_Preview() {
    EduidAppAndroidTheme {
        PinInputField(label = "Enter the PIN code", pinCode = "1234", isPinInvalid = true)
    }
}