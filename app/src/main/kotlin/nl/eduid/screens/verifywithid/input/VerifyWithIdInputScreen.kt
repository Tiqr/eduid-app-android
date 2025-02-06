package nl.eduid.screens.verifywithid.input

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.outlinedTextColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


@Composable
fun VerifyWithIdInputScreen(
    viewModel: VerifyWithIdInputViewModel,
    goBack: () -> Unit,
    goToGeneratedCode: () -> Unit
) = EduIdTopAppBar(
    onBackClicked = goBack
) { padding ->

    VerifyWithIdInputScreenContent(
        goToGeneratedCode = goToGeneratedCode,
        padding = padding,
        generateCode = viewModel::generateCode
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyWithIdInputScreenContent(
    goToGeneratedCode: () -> Unit,
    padding: PaddingValues = PaddingValues(),
    generateCode: (GenerateCodePayload) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val dateState = rememberDatePickerState(selectableDates = object: SelectableDates {
        // Only dates in the past are selectable
        override fun isSelectableDate(utcTimeMillis: Long): Boolean = Date(utcTimeMillis).before(Date())

        override fun isSelectableYear(year: Int): Boolean {
            return year <= Calendar.getInstance().get(Calendar.YEAR)
        }
    })
    var showDatePicker by remember { mutableStateOf(false) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .systemBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdInput_Title_FirstLine_COPY),
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdInput_Title_SecondLine_COPY),
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)

        )
        Spacer(Modifier.height(24.dp))

        Image(
            modifier = Modifier
                .fillMaxWidth(),
            contentDescription = "ID card example",
            painter = painterResource(R.drawable.ic_verify_with_id_example)
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdInput_Explanation_COPY),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Last name
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(vertical = 8.dp),
            text = stringResource(R.string.ConfirmIdentityWithIdInput_InputField_LastName_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        )
        OutlinedTextField(
            colors = outlinedTextColors(),
            value = lastName,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next, keyboardType = KeyboardType.Email
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }),
            onValueChange = {
                lastName = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        // First name(s)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(vertical = 8.dp),
            text = stringResource(R.string.ConfirmIdentityWithIdInput_InputField_FirstNames_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        )
        OutlinedTextField(
            colors = outlinedTextColors(),
            value = firstName,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next, keyboardType = KeyboardType.Email
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
                keyboardController?.hide()
                showDatePicker = true
            }),
            onValueChange = {
                firstName = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
        // Date of birth
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(vertical = 8.dp),
            text = stringResource(R.string.ConfirmIdentityWithIdInput_InputField_DateOfBirth_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        )
        Surface(
            onClick = {
                showDatePicker = true
            },
        ) {
            val selectedDate = dateState.selectedDateMillis
            val dateText = if (selectedDate == null) {
                ""
            } else {
                SimpleDateFormat.getDateInstance().format(Date(selectedDate))
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dateText,
                enabled = false,
                colors = outlinedTextColors().copy(
                    disabledTextColor = outlinedTextColors().unfocusedTextColor,
                    disabledContainerColor = outlinedTextColors().unfocusedContainerColor,
                    disabledPrefixColor = outlinedTextColors().unfocusedPrefixColor,
                    disabledIndicatorColor = outlinedTextColors().unfocusedIndicatorColor,
                    disabledSuffixColor = outlinedTextColors().unfocusedSuffixColor,
                    disabledLabelColor = outlinedTextColors().unfocusedLabelColor,
                    disabledPlaceholderColor = outlinedTextColors().unfocusedPlaceholderColor,
                    disabledLeadingIconColor = outlinedTextColors().unfocusedLeadingIconColor,
                    disabledTrailingIconColor = outlinedTextColors().unfocusedTrailingIconColor,
                    disabledSupportingTextColor = outlinedTextColors().unfocusedSupportingTextColor,
                ),
                onValueChange = {},
                placeholder = {},
            )
        }

        Spacer(Modifier.height(72.dp))
        PrimaryButton(
            enabled = firstName.isNotEmpty() && lastName.isNotEmpty() && dateState.selectedDateMillis != null,
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.ConfirmIdentityWithIdInput_GenerateVerificationCodeButton_COPY),
            onClick = {
                val payload = GenerateCodePayload(
                    firstName = firstName,
                    lastName = lastName,
                    birthDateMillis = dateState.selectedDateMillis!!
                )
                generateCode(payload)
            }
        )
    }
    if (showDatePicker) {
        val stateWhenOpening = remember { dateState.selectedDateMillis }
        DatePickerDialog(
            onDismissRequest = {
                dateState.selectedDateMillis = stateWhenOpening
                showDatePicker = false
            },
            dismissButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis = stateWhenOpening
                    showDatePicker = false
                }) {
                    Text(text = stringResource(R.string.ConfirmIdentityWithIdInput_InputField_DateSelector_Cancel_COPY))

                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                }) {
                    Text(text = stringResource(R.string.ConfirmIdentityWithIdInput_InputField_DateSelector_Confirm_COPY))
                }
            },
        ) {
            DatePicker(
                state = dateState
            )
        }
    }
}

@Composable
@Preview
fun VerifyWithIdInputScreenContent_Preview() {
    EduidAppAndroidTheme {
        VerifyWithIdInputScreenContent(
            goToGeneratedCode = {},
            generateCode = { _ -> }
        )
    }
}