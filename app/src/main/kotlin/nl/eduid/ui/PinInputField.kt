package nl.eduid.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame
import nl.eduid.R
import nl.eduid.ui.theme.EduidAppAndroidTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinInputField(
    label: String?,
    pinCode: String,
    isPinInvalid: Boolean,
    modifier: Modifier = Modifier,
    shouldShowKeyboard: Boolean = true,
    onPinChange: (String) -> Unit = {},
    submitPin: () -> Unit = {},
    pinMaxLength: Int = PIN_MAX_LENGTH,
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    val focusRequester = remember { FocusRequester() }

    if (!label.isNullOrEmpty()) {
        Text(
            text = if (isPinInvalid) "$label*" else label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Spacer(Modifier.height(8.dp))
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(align = Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            for (i in 0 until pinMaxLength) {
                val code = if (pinCode.length - 1 >= i) {
                    pinCode[i].toString()
                } else {
                    ""
                }
                Text(
                    text = if (code.isNotEmpty()) "\u2022" else code,
                    style = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TextFieldDefaults.MinHeight)
                        .border(
                            border = BorderStroke(
                                1.dp, Color(0xFFC3C6CF)
                            ), shape = OutlinedTextFieldDefaults.shape
                        )
                        .weight(1f)
                )
            }
        }
        val options = if (pinCode.length == pinMaxLength) {
            KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
            )
        } else {
            KeyboardOptions(
                imeAction = ImeAction.Next, keyboardType = KeyboardType.Number
            )
        }
        OutlinedTextField(
            value = pinCode,
            onValueChange = {
                onPinChange(it)
                if (it.length == PIN_MAX_LENGTH) {
                    submitPin()
                }
            },
            singleLine = true,
            isError = isPinInvalid,
            keyboardOptions = options,
            keyboardActions = KeyboardActions(onDone = {
                submitPin()
            }),
            modifier = Modifier
                .fillMaxWidth()
                .height(TextFieldDefaults.MinHeight)
                .focusRequester(focusRequester)
                .alpha(0f),
        )
    }

    if (shouldShowKeyboard) {
        LaunchedEffect(focusRequester) {
            awaitFrame()
            focusRequester.requestFocus()
        }
    }
    // Supporting text for error message.
    if (isPinInvalid) {
        Text(
            text = stringResource(R.string.PinAndBioMetrics_EnteredPinNotEqual_COPY),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }
}

const val PIN_MAX_LENGTH = 4

@ExperimentalMaterial3Api
@Preview
@Composable
private fun PinInputField_Preview() {
    EduidAppAndroidTheme {
        PinInputField(label = "Enter the PIN code", pinCode = "1234", isPinInvalid = true)
    }
}