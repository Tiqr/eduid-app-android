package nl.eduid.requestiddetails

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.CheckToSAndPrivacyPolicy
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.ScaffoldWithTopBarBackButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestIdDetailsScreen(
    requestId: () -> Unit,
    onBackClicked: () -> Unit,
    viewModel: RequestIdDetailsViewModel,
) = ScaffoldWithTopBarBackButton(
    onBackClicked = onBackClicked,
    modifier = Modifier
) {
    val inputFormData by viewModel.inputForm.observeAsState(InputForm())

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (content, bottomButton, bottomSpacer) = createRefs()

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(content) {
                    top.linkTo(parent.top)
                }
        ) {

            Text(
                text = stringResource(R.string.request_id_details_screen_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start,
                    color = TextBlack
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            OutlinedTextField(
                value = inputFormData.email,
                isError = (inputFormData.email.length > 2 && !inputFormData.emailValid),
                onValueChange = { newValue -> viewModel.onEmailChange(newValue) },
                label = { Text(stringResource(R.string.request_id_details_screen_email_input_title)) },
                placeholder = { Text(stringResource(R.string.request_id_details_screen_email_input_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = inputFormData.firstName,
                onValueChange = { newValue -> viewModel.onFirstNameChange(newValue) },
                label = { Text(stringResource(R.string.request_id_details_screen_first_name_input_title)) },
                placeholder = { Text(stringResource(R.string.request_id_details_screen_first_name_input_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = inputFormData.lastName,
                onValueChange = { newValue -> viewModel.onLastNameChange(newValue) },
                label = { Text(stringResource(R.string.request_id_details_screen_last_name_input_title)) },
                placeholder = { Text(stringResource(R.string.request_id_details_screen_last_name_input_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            CheckToSAndPrivacyPolicy(
                onAcceptChange = { newValue -> viewModel.onTermsAccepted(newValue) },
                hasAcceptedToC = inputFormData.termsAccepted,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

        }
        PrimaryButton(
            text = stringResource(R.string.enroll_screen_request_id_button),
            onClick = requestId,
            enabled = inputFormData.isFormValid,
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
//        RequestIdDetailsScreen({}, {})
    }
}



