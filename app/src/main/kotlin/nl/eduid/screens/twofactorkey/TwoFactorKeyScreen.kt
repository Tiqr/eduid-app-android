package nl.eduid.screens.twofactorkey

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.KeyInfoCard
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun TwoFactorKeyScreen(
    viewModel: TwoFactorKeyViewModel,
    onDeleteKeyPressed: (id: String) -> Unit,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack
) { padding ->
    viewModel.uiState.errorData?.let { errorData ->
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = viewModel::dismissError
        )
    }

    if (viewModel.uiState.keys.isEmpty()) {
        TwoFactorKeyScreenNoContent(padding)
    } else {
        TwoFactorKeyScreenContent(keyList = viewModel.uiState.keys,
            isLoading = viewModel.uiState.isLoading,
            padding = padding,
            onDeleteKeyPressed = onDeleteKeyPressed,
            onChangeBiometric = { key, biometricFlag ->
                viewModel.changeBiometric(
                    key = key, biometricFlag = biometricFlag
                )
            },
            onExpand = { it -> viewModel.onExpand(it) })
    }
}

@Composable
fun TwoFactorKeyScreenContent(
    keyList: List<IdentityData>,
    isLoading: Boolean = false,
    padding: PaddingValues = PaddingValues(),
    onDeleteKeyPressed: (id: String) -> Unit = {},
    onChangeBiometric: (IdentityData, Boolean) -> Unit = { _, _ -> },
    onExpand: (IdentityData?) -> Unit = { _ -> },
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 24.dp),
) {

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
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
    if (keyList.isNotEmpty()) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.two_fa_key_list_title),
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        Spacer(Modifier.height(6.dp))
    }

    LazyColumn(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(bottom = 24.dp)
    ) {
        items(keyList) { twoFaInfo ->
            KeyInfoCard(
                title = twoFaInfo.title,
                subtitle = twoFaInfo.subtitle,
                keyData = twoFaInfo,
                onDeleteButtonClicked = { onDeleteKeyPressed(twoFaInfo.uniqueKey) },
                onChangeBiometric = onChangeBiometric,
                onExpand = onExpand
            )
        }
    }
}

@Composable
fun TwoFactorKeyScreenNoContent(
    padding: PaddingValues = PaddingValues(),
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 24.dp),
    ) {
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
        keyList = listOf(
            IdentityData(
                uniqueKey = "uniqueId",
                title = "title",
                subtitle = "subtitle",
                account = "account",
                biometricFlag = true,
                isExpanded = true
            )
        ),
    )
}