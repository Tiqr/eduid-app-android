package nl.eduid.screens.emailcodeentry

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.annotatedStringWithBoldParts
import nl.eduid.ui.theme.ColorSupport_Blue_400
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGrey
import nl.eduid.ui.theme.outlinedTextColors

@Composable
fun EmailCodeEntryScreen(
    viewModel: EmailCodeEntryViewModel,
    onBackClicked: () -> Unit,
    goToNextScreen: () -> Unit
) = EduIdTopAppBar(
    onBackClicked = onBackClicked
) {
    val context = LocalContext.current
    val didResentEmailText = stringResource(R.string.LogInWithEmailCode_CodeHasBeenResent_COPY)
    LaunchedEffect(viewModel.uiState.isCodeCorrect) {
        if (viewModel.uiState.isCodeCorrect) {
            goToNextScreen()
        }
    }
    LaunchedEffect(viewModel.uiState.didResendEmail) {
        if (viewModel.uiState.didResendEmail) {
            Toast.makeText(
                context,
                didResentEmailText,
                Toast.LENGTH_LONG
            ).show()
            viewModel.uiState.copy(didResendEmail = false)
        }
    }
    EmailCodeEntryScreenContent(
        userEmail = viewModel.userEmail,
        isCheckingCode = viewModel.uiState.isLoading,
        onBackClicked = onBackClicked,
        submitCode = { code ->
            viewModel.checkEmailCode(code)
        },
        resendCode = {
            viewModel.resendEmailCode()
        },
        dismissError = {
            viewModel.dismissError()
        },
        isCodeIncorrect = viewModel.uiState.isCodeIncorrect,
        isCodeExpired = viewModel.uiState.isCodeExpired,
        isRateLimited = viewModel.uiState.isRateLimited,
        errorData = viewModel.uiState.errorData
    )
}

@Composable
fun EmailCodeEntryScreenContent(
    userEmail: String,
    isCheckingCode: Boolean,
    onBackClicked: () -> Unit,
    submitCode: (String) -> Unit,
    resendCode: () -> Unit,
    dismissError: () -> Unit,
    isCodeIncorrect: Boolean,
    isCodeExpired: Boolean,
    isRateLimited: Boolean,
    errorData: ErrorData?
) = EduIdTopAppBar(
    onBackClicked = onBackClicked
) {
    var codeValue by rememberSaveable { mutableStateOf("") }
    var shouldShowKeyboard by remember { mutableStateOf(true) }

    if (isCodeIncorrect || isCodeExpired || isRateLimited || errorData != null) {
        val title: String
        val message: String
        when {
            isCodeIncorrect -> {
                title = stringResource(R.string.ResponseErrors_EmailCodeError_Title_COPY)
                message = stringResource(R.string.ResponseErrors_EmailCodeError_Incorrect_COPY)
            }
            isCodeExpired -> {
                title = stringResource(R.string.ResponseErrors_EmailCodeError_Title_COPY)
                message = stringResource(R.string.ResponseErrors_EmailCodeError_Expired_COPY)
            }
            isRateLimited -> {
                title = stringResource(R.string.ResponseErrors_EmailCodeError_Title_COPY)
                message = stringResource(R.string.ResponseErrors_EmailCodeError_RateLimited_COPY)
            }
            errorData != null -> {
                val context = LocalContext.current
                title = errorData.title(context)
                message = errorData.message(context)
            }
            else -> {
                throw RuntimeException("Unhandled error state in EmailCodeEntryScreenContent")
            }
        }
        AlertDialogWithSingleButton(
            title = title ,
            explanation = message,
            buttonLabel = stringResource(R.string.Generic_RequestError_CloseButton_COPY),
            onDismiss = {
                dismissError()
                if (isRateLimited) {
                    onBackClicked()
                }
                if (isCodeIncorrect) {
                    codeValue = ""
                    shouldShowKeyboard = true
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.LogInWithEmailCode_CheckYourEmail_COPY),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )
        val codeSentToEmailString = annotatedStringWithBoldParts(
            stringResource(R.string.LogInWithEmailCode_EnterTheCodeSentTo_COPY, userEmail),
            userEmail
        )
        Text(
            text = codeSentToEmailString,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(
            modifier = Modifier.height(24.dp)
        )
        EmailCodeEntryField(
            code = codeValue,
            isCodeInvalid = false,
            modifier = Modifier.fillMaxWidth(),
            shouldShowKeyboard = shouldShowKeyboard,
            isInputEnabled = !isCheckingCode,
            onCodeChange = {
                codeValue = it
            },
            submitCode = {
                shouldShowKeyboard = false
                submitCode(codeValue)
            }
        )
        Spacer(
            modifier = Modifier.height(24.dp)
        )
        val resendEmailPart1 = stringResource(R.string.LogInWithEmailCode_Problems_COPY)
        val resendEmailPart2 = stringResource(R.string.LogInWithEmailCode_ResendTheCode_COPY)
        val resendEmailString = buildAnnotatedString {
            append(resendEmailPart1)
            append(" ")
            withLink(
                LinkAnnotation.Clickable(
                    "resend_email", linkInteractionListener = {
                        if (!isCheckingCode) {
                            resendCode()
                        }
                    }, styles = TextLinkStyles(
                        style = SpanStyle(
                            color = if (isCheckingCode) TextGrey else ColorSupport_Blue_400,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                )
            ) {
                append(resendEmailPart2)
            }
        }
        Text(
            textAlign = TextAlign.Center,
            text = resendEmailString,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(
            modifier = Modifier.fillMaxHeight()
        )
    }
}

@Composable
fun EmailCodeEntryField(
    code: String,
    isCodeInvalid: Boolean,
    modifier: Modifier = Modifier,
    shouldShowKeyboard: Boolean = true,
    isInputEnabled: Boolean = true,
    onCodeChange: (String) -> Unit = {},
    submitCode: () -> Unit = {},
    codeLength: Int = 6,
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(align = Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            for (i in 0 until codeLength) {
                val codeCharacter = if (code.length - 1 >= i) {
                    code[i].toString()
                } else {
                    ""
                }
                Text(
                    text = codeCharacter,
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
                        .alpha(if (isInputEnabled) 1f else 0.4f)
                        .wrapContentHeight()
                        .weight(1f)
                )
            }
        }
        val options = if (code.length == codeLength) {
            KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
            )
        } else {
            KeyboardOptions(
                imeAction = ImeAction.Next, keyboardType = KeyboardType.Number
            )
        }
        OutlinedTextField(
            colors = outlinedTextColors(),
            value = code,
            enabled = isInputEnabled,
            onValueChange = {
                onCodeChange(it)
                if (it.length == codeLength) {
                    submitCode()
                }
            },
            singleLine = true,
            isError = isCodeInvalid,
            keyboardOptions = options,
            keyboardActions = KeyboardActions(onDone = {
                submitCode()
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
    if (isCodeInvalid) {
        Text(
            text = stringResource(R.string.PinAndBioMetrics_EnteredPinNotEqual_COPY),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }
}

@Preview
@Composable
private fun Preview_EmailCodeEntryScreen() {
    EduidAppAndroidTheme {
        EmailCodeEntryScreenContent(
            userEmail = "test+test@email.com",
            isCheckingCode = false,
            onBackClicked = {},
            submitCode = {},
            resendCode = {},
            dismissError = {},
            isCodeIncorrect = false,
            isCodeExpired = false,
            isRateLimited = false,
            errorData = null
        )
    }
}