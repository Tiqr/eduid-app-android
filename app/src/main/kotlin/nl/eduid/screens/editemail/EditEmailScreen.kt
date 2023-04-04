package nl.eduid.screens.editemail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.ButtonBlue
import nl.eduid.ui.theme.ButtonBorderGrey
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.ButtonRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGrey

@Composable
fun EditEmailScreen(
    viewModel: EditEmailViewModel,
    onSaveNewEmailRequested: (newEmail: String) -> Unit,
    goBack: () -> Unit,
) {
    val email by viewModel.emailInput.observeAsState("")
    val emailValid by viewModel.emailValid.observeAsState(false)
    val uiState by viewModel.uiState.observeAsState(EditEmailViewModel.UiState.Idle)
    val uiError by viewModel.uiError.observeAsState("")

    EditEmailScreenContent(
        onNewEmailRequestClicked = { viewModel.requestEmailChangeClicked(it, onSaveNewEmailRequested) },
        email = email,
        emailValid = emailValid,
        onEmailTextChange = { viewModel.onEmailChange(it) },
        uiState = uiState,
        uiError = uiError,
        goBack = goBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EditEmailScreenContent(
    onNewEmailRequestClicked: (newEmail: String) -> Unit = {},
    email: String,
    emailValid: Boolean,
    onEmailTextChange: (String) -> Unit = {},
    uiState: EditEmailViewModel.UiState,
    uiError: String,
    goBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(top = 42.dp, start = 26.dp, end = 26.dp),
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.back_button_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .size(width = 46.dp, height = 46.dp)
                            .clickable {
                                goBack.invoke()
                            },
                        alignment = Alignment.Center
                    )
                },
                title = {
                    Image(
                        painter = painterResource(R.drawable.ic_top_logo),
                        contentDescription = "",
                        modifier = Modifier.size(width = 122.dp, height = 46.dp),
                        alignment = Alignment.Center
                    )
                },
            )
        },
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 26.dp, end = 26.dp)
        ) {
            val (body, bottomColumn, loader) = createRefs()
            val keyboardController = LocalSoftwareKeyboardController.current

            if (uiState == EditEmailViewModel.UiState.Loading) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(80.dp)
                        .width(80.dp)
                        .constrainAs(loader) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top, margin = 20.dp)
                        }
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .constrainAs(body) {
                            linkTo(parent.top, bottomColumn.top, bias = 0F)
                        }
                        .verticalScroll(rememberScrollState())

                ) {
                    Spacer(Modifier.height(36.dp))
                    Text(
                        style = MaterialTheme.typography.titleLarge.copy(
                            textAlign = TextAlign.Start,
                            color = ButtonGreen
                        ),
                        text = stringResource(R.string.edit_email_title),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                        text = stringResource(R.string.edit_email_subtitle),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(36.dp))
                    OutlinedTextField(
                        value = email,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        isError = (email.length > 2 && !emailValid),
                        onValueChange = { onEmailTextChange(it) },
                        label = { Text(stringResource(R.string.edit_email_new_email_title)) },
                        placeholder = { Text(stringResource(R.string.request_id_details_screen_email_input_hint)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (uiError.isNotBlank()) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start, color = ButtonRed),
                            text = uiError,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.constrainAs(bottomColumn) {
                        bottom.linkTo(parent.bottom, margin = 24.dp)
                    },
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PrimaryButton(
                                modifier = Modifier.widthIn(min = 140.dp),
                                text = stringResource(R.string.edit_email_cancel_button),
                                onClick = goBack,
                                buttonBackgroundColor = Color.Transparent,
                                buttonTextColor = TextGrey,
                                buttonBorderColor = ButtonBorderGrey,
                            )
                            PrimaryButton(
                                modifier = Modifier.widthIn(min = 140.dp),
                                text = stringResource(R.string.edit_email_confirm_button),
                                onClick = { onNewEmailRequestClicked(email) },
                                buttonBackgroundColor = ButtonBlue,
                                buttonTextColor = Color.White,
                                enabled = emailValid,
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun PreviewEditEmailScreenContent() = EduidAppAndroidTheme {
    EditEmailScreenContent(
        email = "",
        emailValid = true,
        onEmailTextChange = {},
        uiState = EditEmailViewModel.UiState.Idle,
        goBack = { },
        uiError = "ERROR",
    )
}