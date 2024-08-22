package nl.eduid.screens.security

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.flags.FeatureFlag
import nl.eduid.flags.RuntimeBehavior
import nl.eduid.ui.AddSecurityField
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EditableSecurityField
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun SecurityRoute(
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
        dismissError = viewModel::dismissError,
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
        .systemBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
) {
    if (securityInfo.errorData != null) {
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = securityInfo.errorData.title(context),
            explanation = securityInfo.errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = dismissError,
        )
    }

    Text(
        style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSecondary),
        text = stringResource(R.string.Security_Title_COPY),
        modifier = Modifier.padding(top = 16.dp),
    )
    Text(
        style = MaterialTheme.typography.titleLarge,
        text = stringResource(R.string.Security_SubTitle_COPY),
    )
    if (securityInfo.isLoading) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )
    }
    Spacer(Modifier.height(8.dp))
    securityInfo.twoFAProvider?.let {
        EditableSecurityField(
            title = stringResource(R.string.Security_TwoFAKey_COPY),
            annotatedSubtitle = buildAnnotatedString {
                append(stringResource(R.string.Security_ProvidedBy_COPY))
                append(" ")
                pushStyle(
                    MaterialTheme.typography.bodyLarge
                        .copy(
                            fontWeight = FontWeight.SemiBold,
                        ).toSpanStyle(),
                )
                append(it)
            },
            leadingIcon = R.drawable.ic_security_key,
            modifier = Modifier.clickable { on2FaClicked() },
        )
    }
    if (securityInfo.hasPassword) {
        EditableSecurityField(
            title = stringResource(R.string.Security_ChangePassword_COPY),
            subtitle = "****",
            leadingIcon = R.drawable.ic_security_password,
            modifier = Modifier.clickable { onConfigurePasswordClicked() },
        )
    }

    EditableSecurityField(
        title = stringResource(R.string.Security_UseMagicLink_COPY),
        subtitle = securityInfo.email,
        leadingIcon = R.drawable.ic_security_email_link,
        modifier = Modifier.clickable { onEditEmailClicked() },
    )

    if (!securityInfo.hasPassword || RuntimeBehavior.isFeatureEnabled(FeatureFlag.SHOW_ADD_SECURITY_KEY)) {
        Text(
            text = stringResource(R.string.Security_OtherMethods_COPY),
            style = MaterialTheme.typography.bodyLarge,
        )
    }

    if(RuntimeBehavior.isFeatureEnabled(FeatureFlag.SHOW_ADD_SECURITY_KEY)){
        AddSecurityField(
            title = stringResource(R.string.Webauthn_SetTitle_COPY),
            leadingIcon = R.drawable.ic_security_key,
            modifier = Modifier.clickable { on2FaClicked() },
        )
    }

    if (!securityInfo.hasPassword) {
        AddSecurityField(
            title = stringResource(R.string.Password_AddTitle_COPY),
            leadingIcon = R.drawable.ic_security_password,
            modifier = Modifier.clickable { onConfigurePasswordClicked() },
        )
    }
}

@Preview
@Composable
private fun PreviewSecurityScreenContent() = EduidAppAndroidTheme {
    SecurityScreenContent(
        securityInfo = SecurityScreenData(
            twoFAProvider = "test.eduid.nl",
            email = "librarian@unseenuniveristy.disk",
            hasPassword = true,
        ),
    )
}

@Preview
@Composable
private fun Preview_OnlyEmailSecurityScreenContent() = EduidAppAndroidTheme {
    SecurityScreenContent(
        securityInfo = SecurityScreenData(
            email = "librarian@unseenuniveristy.disk",
        ),
    )
}