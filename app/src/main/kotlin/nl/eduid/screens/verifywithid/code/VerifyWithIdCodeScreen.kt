package nl.eduid.screens.verifywithid.intro

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.eduid.R
import nl.eduid.di.model.ControlCode
import nl.eduid.screens.verifywithid.code.VerifyWithIdCodeViewModel
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.theme.AlertWarningBackground
import nl.eduid.ui.theme.ColorAlertRed
import nl.eduid.ui.theme.ColorScale_Gray_200
import nl.eduid.ui.theme.ColorScale_Gray_400
import nl.eduid.ui.theme.ColorSupport_Blue_400
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.outlinedTextColors


@Composable
fun VerifyWithIdCodeScreen(
    viewModel: VerifyWithIdCodeViewModel,
    editCode: (ControlCode) -> Unit,
    goToPersonalInfo: () -> Unit
) = EduIdTopAppBar(
    onBackClicked = goToPersonalInfo
) { padding ->
    val uriHandler = LocalUriHandler.current
    if (viewModel.controlCode == null) {
        goToPersonalInfo()
        return@EduIdTopAppBar
    }
    LaunchedEffect(viewModel.uiState.codeDeleted) {
        if (viewModel.uiState.codeDeleted) {
            goToPersonalInfo()
        }
    }
    VerifyWithIdCodeScreenContent(
        padding = padding,
        isLoading = viewModel.uiState.isLoading,
        code = viewModel.controlCode.code,
        firstName = viewModel.controlCode.firstName,
        lastName = viewModel.controlCode.lastName,
        dayOfBirth = viewModel.controlCode.dayOfBirth,
        editVerificationCode = {
            editCode(viewModel.controlCode)
        },
        deleteVerificationCode = {
            viewModel.deleteCode()
        },
        openShowServiceDesksUrl = { uri ->
            uriHandler.openUri(uri)
        },
        goToPersonalInfo = goToPersonalInfo
    )
}

@Composable
fun VerifyWithIdCodeScreenContent(
    padding: PaddingValues = PaddingValues(),
    isLoading: Boolean,
    code: String,
    firstName: String?,
    lastName: String?,
    dayOfBirth: String?,
    editVerificationCode: () -> Unit,
    deleteVerificationCode: () -> Unit,
    openShowServiceDesksUrl: (String) -> Unit,
    goToPersonalInfo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(padding)
            .systemBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdCode_Title_COPY),
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        Spacer(Modifier.height(24.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .background(AlertWarningBackground, shape = RoundedCornerShape(6.dp))
                .border(width = 1.dp, color = ColorScale_Gray_400, shape = RoundedCornerShape(6.dp))
                .padding(vertical = 15.dp)
        ) {
            Text(
                text = code,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 45.sp,
                    letterSpacing = 6.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdCode_Explanation_COPY),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .background(AlertWarningBackground, shape = RoundedCornerShape(6.dp))
                .border(width = 1.dp, color = ColorScale_Gray_400, shape = RoundedCornerShape(6.dp))
                .padding(vertical = 24.dp, horizontal = 24.dp)
        ) {
            val darkYellow = Color(0xFFF3E278)
            val textFieldColors = outlinedTextColors().copy(
                disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = darkYellow,
                disabledIndicatorColor = darkYellow
            )
            Column {
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
                    colors = textFieldColors,
                    value = lastName ?: "",
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
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
                    colors = textFieldColors,
                    value = firstName ?: "",
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
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
                OutlinedTextField(
                    colors = textFieldColors,
                    value = dayOfBirth ?: "",
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))
                val label = stringResource(R.string.ConfirmIdentityWithIdCode_MadeATypo_Label_COPY)
                val linkedPart = stringResource(R.string.ConfirmIdentityWithIdCode_MadeATypo_Link_COPY)
                val labelAndLink = buildAnnotatedString {
                    append(label)
                    append(" ")
                    withLink(
                        link = LinkAnnotation.Clickable(
                            tag = "support_link",
                            linkInteractionListener = {
                                editVerificationCode()
                            },
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = if (isLoading) ColorScale_Gray_400 else ColorSupport_Blue_400,
                                    textDecoration = TextDecoration.Underline,
                                )
                            )
                        )
                    ) {
                        append(linkedPart)
                    }
                }
                Text(
                    text = labelAndLink,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdCode_WhatsNext_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdCode_ScheduleAnAppointment_COPY),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        val serviceDeskUrl = stringResource(R.string.ConfirmIdentityWithIdCode_ServiceDeskUrl_COPY)
        PrimaryButton(
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.ConfirmIdentityWithIdCode_ShowServiceDesksButton_COPY),
            onClick = {
                openShowServiceDesksUrl(serviceDeskUrl)
            }
        )
        Spacer(Modifier.height(20.dp))
        SecondaryButton(
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.ConfirmIdentityWithIdCode_GoToHomePageButton_COPY),
            onClick = goToPersonalInfo
        )
        Spacer(Modifier.height(20.dp))
        VerticalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = ColorScale_Gray_200
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdCode_ProveIdentityOtherWay_COPY),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))
        OutlinedButton(
            enabled = !isLoading,
            border = BorderStroke(
                width = 1.dp,
                color = if (isLoading) {
                    ColorScale_Gray_400
                } else {
                    ColorAlertRed
                }
            ),
            shape = RoundedCornerShape(CornerSize(6.dp)),
            onClick = deleteVerificationCode,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isLoading) ColorScale_Gray_400 else ColorAlertRed,
            ),
            modifier = Modifier.fillMaxWidth()
                    .sizeIn(minHeight = 48.dp)
        ) {
            Text(
                text = stringResource(R.string.ConfirmIdentityWithIdCode_DeleteVerificationCodeButton_COPY),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (isLoading) ColorScale_Gray_400 else ColorAlertRed,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
@Preview
fun VerifyWithIdCodeScreenContent_Preview() {
    EduidAppAndroidTheme {
        VerifyWithIdCodeScreenContent(
            code = "123456",
            isLoading = true,
            firstName = "First name",
            lastName = "Last name",
            dayOfBirth = "1993-12-24",
            editVerificationCode = {},
            deleteVerificationCode = {},
            openShowServiceDesksUrl = {},
            goToPersonalInfo = {}
        )
    }
}