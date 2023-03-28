package nl.eduid.screens.editemail

import android.util.Patterns
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.screens.requestiddetails.InputForm
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.ButtonBlue
import nl.eduid.ui.theme.ButtonBorderGrey
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGrey

@Composable
fun EditEmailScreen(
    viewModel: EditEmailViewModel,
    onSaveNewEmailClicked: (newEmail: String) -> Unit,
    goBack: () -> Unit,
) {
    val email by viewModel.emailInput.observeAsState("")
    val emailValid by viewModel.emailValid.observeAsState(false)

    EditEmailScreenContent(
        onSaveNewEmailClicked = onSaveNewEmailClicked,
        email = email,
        emailValid = emailValid,
        onEmailChange = { viewModel.onEmailChange(it) },
        goBack = goBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EditEmailScreenContent(
    onSaveNewEmailClicked: (newEmail: String) -> Unit = {},
    email: String,
    emailValid: Boolean,
    onEmailChange: (String) -> Unit = {},
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
            val (body, bottomColumn) = createRefs()
            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current

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
                    onValueChange = { onEmailChange(it) },
                    label = { Text(stringResource(R.string.edit_email_new_email_title)) },
                    placeholder = { Text(stringResource(R.string.request_id_details_screen_email_input_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )
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
                            onClick = { onSaveNewEmailClicked(email) },
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


@Preview
@Composable
private fun PreviewEditEmailScreenContent() = EduidAppAndroidTheme {
    EditEmailScreenContent(
        onSaveNewEmailClicked = { },
        goBack = { }, email = "", emailValid = true, onEmailChange = {},
    )
}