package nl.eduid.screens.requestidrecovery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.ScaffoldWithTopBarBackButton
import nl.eduid.ui.theme.EduidAppAndroidTheme

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RequestIdRecoveryScreen(
    onVerifyPhoneNumberClicked: (phoneNumber: String) -> Unit,
    onBackClicked: () -> Unit,
    viewModel: RequestIdRecoveryViewModel,
) = ScaffoldWithTopBarBackButton(
    onBackClicked = onBackClicked, modifier = Modifier
) {
    val recoveryPhoneInput by viewModel.recoveryPhoneInput.observeAsState("")
    val keyboardController = LocalSoftwareKeyboardController.current

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (content, bottomButton, bottomSpacer) = createRefs()


        Column(horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(content) {
                    top.linkTo(parent.top)
                }) {

            Text(
                text = stringResource(R.string.request_id_recovery_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Text(
                text = stringResource(R.string.request_id_recovery_text_code),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )
            Text(
                text = stringResource(R.string.request_id_recovery_input_hint),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = recoveryPhoneInput,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                onValueChange = { newValue -> viewModel.onPhoneNumberChanged(newValue) },
                modifier = Modifier.fillMaxWidth()
            )


        }
        PrimaryButton(
            text = stringResource(R.string.request_id_recovery_button),
            onClick = { onVerifyPhoneNumberClicked("") },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomButton) {
                    bottom.linkTo(bottomSpacer.top)
                },
        )
        Spacer(
            Modifier
                .height(40.dp)
                .constrainAs(bottomSpacer) {
                    bottom.linkTo(parent.bottom)
                },
        )
    }
}

@Preview()
@Composable
private fun PreviewEnroll() {
    EduidAppAndroidTheme {
        RequestIdRecoveryScreen({}, {}, RequestIdRecoveryViewModel())
    }
}




