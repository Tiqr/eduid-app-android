package nl.eduid.screens.verifywithid.input

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme


@Composable
fun VerifyWithIdInputScreen(
    goBack: () -> Unit,
    goToGeneratedCode: () -> Unit
) = EduIdTopAppBar(
    onBackClicked = goBack
) { padding ->

    VerifyWithIdInputScreenContent(
        goToGeneratedCode = goToGeneratedCode,
        padding = padding
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyWithIdInputScreenContent(
    goToGeneratedCode: () -> Unit,
    padding: PaddingValues = PaddingValues(),
) {
    val dateState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
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
        Spacer(Modifier.height(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth().align(Alignment.Start).padding(vertical = 8.dp),
            text = stringResource(R.string.ConfirmIdentityWithIdInput_InputField_LastName_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            ),

        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "test",
            onValueChange = {},
            placeholder = { Text(stringResource(R.string.Login_FamilyNamePlaceholder_COPY)) },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                showDatePicker = true
                            }
                        }
                    }
                }
        )

        Spacer(Modifier.height(72.dp))
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.ConfirmIdentityWithIdInput_GenerateVerificationCodeButton_COPY),
            onClick = goToGeneratedCode
        )
    }
    if (showDatePicker) {
        Surface(onClick = {
            showDatePicker = false
        }) {
            // TODO make this look nice
            DatePicker(
                modifier = Modifier.padding(padding)
                    .padding(horizontal = 16.dp),
                state = dateState,
                showModeToggle = false
            )
        }
    }
}

@Composable
@Preview
fun VerifyWithIdInputScreenContent_Preview() {
    EduidAppAndroidTheme {
        VerifyWithIdInputScreenContent(
            goToGeneratedCode = {}
        )
    }
}