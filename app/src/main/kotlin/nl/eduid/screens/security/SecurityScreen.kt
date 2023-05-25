package nl.eduid.screens.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.InfoField
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun SecurityScreen(
    viewModel: SecurityViewModel,
    onConfigurePasswordClick: () -> Unit,
    onEditEmailClicked: () -> Unit,
    on2FaClicked: () -> Unit,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack,
) {
    SecurityScreenContent(
        securityInfo = viewModel.uiState,
        padding = it,
        onConfigurePasswordClicked = onConfigurePasswordClick,
        onEditEmailClicked = onEditEmailClicked,
        on2FaClicked = on2FaClicked,
        dismissError = viewModel::dismissError
    )
}

@Composable
fun SecurityScreenContent(
    securityInfo: SecurityScreenData,
    padding: PaddingValues = PaddingValues(),
    onConfigurePasswordClicked: () -> Unit = {},
    onEditEmailClicked: () -> Unit = {},
    on2FaClicked: () -> Unit = {},
    dismissError: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .navigationBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
) {
    if (securityInfo.errorData != null) {
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = securityInfo.errorData.title(context),
            explanation = securityInfo.errorData.message(context),
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = dismissError
        )
    }

    Text(
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Start, color = ButtonGreen
        ), text = stringResource(R.string.security_title), modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
        text = stringResource(R.string.security_subtitle),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(36.dp))
    if (securityInfo.isLoading) {
        Spacer(Modifier.height(24.dp))
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    }
    InfoField(
        title = stringResource(R.string.security_2fa_key),
        subtitle = if (securityInfo.twoFAProvider != null) {
            stringResource(
                R.string.security_provided_by_eduid, securityInfo.twoFAProvider
            )
        } else {
            stringResource(
                R.string.security_provided_by_na,
            )
        },
        onClick = on2FaClicked,
        endIcon = R.drawable.shield_tick_blue,
        label = stringResource(R.string.security_sign_in_methods)
    )
    Spacer(Modifier.height(16.dp))
    InfoField(
        title = stringResource(R.string.security_send_a_magic_link_to),
        subtitle = securityInfo.email,
        onClick = onEditEmailClicked,
        endIcon = R.drawable.edit_icon
    )
    Spacer(Modifier.height(16.dp))
    InfoField(
        title = if (securityInfo.hasPassword) {
            stringResource(R.string.security_change_password)
        } else {
            stringResource(R.string.security_add_a_password)
        }, subtitle = if (securityInfo.hasPassword) {
            "****"
        } else {
            ""
        }, onClick = onConfigurePasswordClicked, endIcon = R.drawable.edit_icon
    )
}


@Preview
@Composable
private fun PreviewSecurityScreenContent() = EduidAppAndroidTheme {
    SecurityScreenContent(
        securityInfo = SecurityScreenData(),
    )
}