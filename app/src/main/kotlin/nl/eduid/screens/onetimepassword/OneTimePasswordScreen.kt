package nl.eduid.screens.onetimepassword

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.screens.authorize.EduIdAuthenticationViewModel
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.ColorMain_Green_400
import nl.eduid.ui.theme.EduidAppAndroidTheme
import org.tiqr.data.model.ChallengeCompleteFailure
import org.tiqr.data.model.ChallengeCompleteOtpResult

@Composable
fun OneTimePasswordScreen(
    viewModel: EduIdAuthenticationViewModel,
    onCancel: () -> Unit,
) = EduIdTopAppBar(
    withBackIcon = false
) {

    val otp by viewModel.otp.observeAsState(null)
    val userId by viewModel.userId.observeAsState(null)

    OneTimePasswordContent(
        userId = userId,
        otp = otp,
        padding = it,
        close = onCancel,
    )
}

@Composable
private fun OneTimePasswordContent(
    userId: String?,
    otp: ChallengeCompleteOtpResult<ChallengeCompleteFailure>?,
    padding: PaddingValues = PaddingValues(),
    close: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start, color = ColorMain_Green_400
                ),
                text = stringResource(R.string.OneTimePassword_Title_COPY),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(R.string.OneTimePassword_Description_COPY),
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (otp == null) {
                Spacer(modifier = Modifier.height(20.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(64.dp)
                        .width(64.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else if (!otp.isSuccess) {
                AlertDialogWithSingleButton(
                    title = stringResource(id = R.string.OneTimePassword_GenerateError_Title_COPY),
                    explanation = stringResource(id = R.string.OneTimePassword_GenerateError_Description_COPY),
                    buttonLabel = stringResource(R.string.Button_OK_COPY),
                    onDismiss = close
                )
            } else {
                val otpCode = otp.value
                Row {
                    otpCode.forEachIndexed { index, character ->
                        Text(
                            text = character.toString(),
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
                                .wrapContentHeight()
                        )
                        if (index != otpCode.length - 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = stringResource(R.string.OneTimePassword_YourId_COPY),
                    )
                    Text(
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        text = " ${userId ?: ""}",
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.OneTimePassword_PinNotVerified_Title_COPY),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.OneTimePassword_PinNotVerified_Description_COPY),
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            PrimaryButton(
                modifier = Modifier.widthIn(min = 140.dp),
                text = stringResource(R.string.OneTimePassword_CloseButton_COPY),
                onClick = {
                    close()
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewOneTimePasswordScreen() = EduidAppAndroidTheme {
    OneTimePasswordContent(otp = ChallengeCompleteOtpResult.success("123456"), userId = "userID")
}