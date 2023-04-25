package nl.eduid.screens.twofactorkey

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import nl.eduid.ui.InfoTab
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun TwoFactorKeyScreen(
    viewModel: TwoFactorKeyViewModel,
    onDeleteKeyPressed: (id: String) -> Unit,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack
) {
    val uiState by viewModel.uiState.observeAsState(TwoFactorData())
    if (uiState.keys.isEmpty()) {
        TwoFactorKeyScreenNoContent()
    } else {
        TwoFactorKeyScreenContent(
            uiState = uiState.keys,
            isLoading = uiState.isLoading,
            onDeleteKeyPressed = onDeleteKeyPressed,
        )
    }
}

@Composable
fun TwoFactorKeyScreenContent(
    uiState: List<IdentityData>,
    isLoading: Boolean = false,
    onDeleteKeyPressed: (id: String) -> Unit,
) = Column(
    verticalArrangement = Arrangement.Bottom,
    modifier = Modifier.verticalScroll(rememberScrollState())
) {
    Spacer(Modifier.height(36.dp))

    Text(
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Start, color = ButtonGreen
        ), text = stringResource(R.string.two_fa_key_title), modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
        text = stringResource(R.string.two_fa_key_subtitle),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(36.dp))
    if (isLoading) {
        Spacer(Modifier.height(24.dp))
        CircularProgressIndicator(
            modifier = Modifier
                .height(80.dp)
                .width(80.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
    } else {
        uiState.forEachIndexed { id, twoFaInfo ->
            InfoTab(
                header = if (id < 1) "Your Keys" else "",
                startIconLargeUrl = twoFaInfo.providerLogoUrl,
                title = twoFaInfo.title,
                subtitle = twoFaInfo.subtitle,
                onClick = { },
                onDeleteButtonClicked = { onDeleteKeyPressed(twoFaInfo.uniqueKey) },
                endIcon = R.drawable.chevron_down,
                twoFactorData = twoFaInfo,
            )
        }
    }
}

@Composable
fun TwoFactorKeyScreenNoContent(
) = Column(
    verticalArrangement = Arrangement.Bottom,
    modifier = Modifier.verticalScroll(rememberScrollState())
) {
    Spacer(Modifier.height(36.dp))

    Text(
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Start, color = ButtonGreen
        ), text = stringResource(R.string.two_fa_key_title), modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
        text = stringResource(R.string.two_fa_key_empty),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(36.dp))
}

@Preview
@Composable
private fun PreviewSecurityScreenContent() = EduidAppAndroidTheme {
    TwoFactorKeyScreenContent(
        uiState = listOf(),
    ) {}
}