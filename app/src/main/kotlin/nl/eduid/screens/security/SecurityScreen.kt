package nl.eduid.screens.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
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
    val securityInfo by viewModel.securityInfo.observeAsState(SecurityScreenData())
    SecurityScreenContent(
        securityInfo = securityInfo,
        onConfigurePasswordClicked = onConfigurePasswordClick,
        onEditEmailClicked = onEditEmailClicked,
        on2FaClicked = on2FaClicked,
    )
}

@Composable
fun SecurityScreenContent(
    securityInfo: SecurityScreenData,
    onConfigurePasswordClicked: () -> Unit = {},
    onEditEmailClicked: () -> Unit = {},
    on2FaClicked: () -> Unit = {},
) = Column(
    verticalArrangement = Arrangement.Bottom,
    modifier = Modifier
        .verticalScroll(rememberScrollState())
) {
    Spacer(Modifier.height(36.dp))

    Text(
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Start,
            color = ButtonGreen
        ),
        text = stringResource(R.string.security_title),
        modifier = Modifier
            .fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
        text = stringResource(R.string.security_subtitle),
        modifier = Modifier
            .fillMaxWidth()
    )
    Spacer(Modifier.height(36.dp))
    if (securityInfo.email.isBlank()) {
        Spacer(Modifier.height(24.dp))
        CircularProgressIndicator(
            modifier = Modifier
                .height(80.dp)
                .width(80.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
    } else {
        InfoField(
            title = stringResource(R.string.security_2fa_key),
            subtitle = stringResource(R.string.security_provided_by_eduid),
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
            },
            subtitle = if (securityInfo.hasPassword) {
                "****"
            } else {
                ""
            },
            onClick = onConfigurePasswordClicked,
            endIcon = R.drawable.edit_icon
        )
        Spacer(Modifier.height(16.dp))
    }
}


@Preview
@Composable
private fun PreviewSecurityScreenContent() = EduidAppAndroidTheme {
    SecurityScreenContent(
        securityInfo = SecurityScreenData(),
    )
}