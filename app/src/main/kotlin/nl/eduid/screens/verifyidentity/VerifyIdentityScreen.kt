package nl.eduid.screens.verifyidentity

import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.annotatedStringWithBoldParts
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.HighlightBackground
import timber.log.Timber

@Composable
fun VerifyIdentityScreen(
    viewModel: VerifyIdentityViewModel,
    goToBankSelectionScreen: () -> Unit,
    goToFallbackMethodScreen: () -> Unit,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack,
) { padding ->
    val launcher = rememberLauncherForActivityResult(contract = ExternalAccountLinkingContract(), onResult = { _ ->
        /**
         * We don't have to explicitly handle the result intent. The deep linking will automatically open the [AccountLinkedScreen()] and
         * ensure the backstack is correct.
         */
    })

    LaunchedEffect(viewModel.uiState.launchIntent) {
        viewModel.uiState.launchIntent?.let { intent ->
            launcher.launch(intent)
            viewModel.clearLaunchIntent()
        }
    }

    VerifyIdentityScreenContent(
        isLoading = viewModel.uiState.isLoading,
        errorData = viewModel.uiState.errorData,
        dismissError = viewModel::dismissError,
        goToBankSelectionScreen = goToBankSelectionScreen,
        requestInstitutionLink = viewModel::requestInstitutionLink,
        requestEidasLink = viewModel::requestEidasLink,
        goToFallbackMethodScreen = goToFallbackMethodScreen,
        padding = padding,
    )
}

@Composable
fun VerifyIdentityScreenContent(
    isLoading: Boolean,
    errorData: ErrorData?,
    dismissError: () -> Unit,
    goToBankSelectionScreen: () -> Unit,
    requestInstitutionLink: () -> Unit,
    requestEidasLink: () -> Unit,
    goToFallbackMethodScreen: () -> Unit,
    padding: PaddingValues = PaddingValues(),
) {
    val context = LocalContext.current

    if (errorData != null) {
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = dismissError,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(padding)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        ) {
            Text(
                text = stringResource(R.string.VerifyIdentity_Title_COPY),
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSecondary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.VerifyIdentity_Subtitle_COPY),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))

            HighlightedVerifyIdentityOption(onClick = requestInstitutionLink, isLoading = isLoading)

            Column(
                modifier = Modifier
                    .padding(vertical = 20.dp, horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val noInstitution = annotatedStringWithBoldParts(
                    stringResource(R.string.VerifyIdentity_IfYouDontOwnAnAccount_Text_COPY),
                    stringResource(R.string.VerifyIdentity_IfYouDontOwnAnAccount_BoldPart_COPY),
                )
                Text(
                    text = noInstitution,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                )

                VerifyIdentityOption(
                    title = stringResource(R.string.VerifyIdentity_Button_UseADutchBank_COPY),
                    icon = R.drawable.ic_verify_via_idin,
                    isLoading = isLoading,
                    onClick = goToBankSelectionScreen,
                )

                VerifyIdentityOption(
                    title = stringResource(R.string.VerifyIdentity_Button_UseAEuropeanId_COPY),
                    icon = R.drawable.ic_eidas,
                    isLoading = isLoading,
                    onClick = requestEidasLink,
                )

                VerifyIdentityOption(
                    title = stringResource(R.string.VerifyIdentity_Button_ContactServiceDesk_COPY),
                    icon = R.drawable.ic_verify_support,
                    isLoading = isLoading,
                    onClick = goToFallbackMethodScreen,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HighlightedVerifyIdentityOption(onClick: () -> Unit, isLoading: Boolean) = Column(
    modifier = Modifier
        .background(color = HighlightBackground)
        .padding(vertical = 20.dp, horizontal = 10.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
) {
    val viaInstitution = annotatedStringWithBoldParts(
        stringResource(R.string.VerifyIdentity_DoYouOwnAnAccount_Text_COPY),
        stringResource(R.string.VerifyIdentity_DoYouOwnAnAccount_BoldPart_COPY),
    )
    Text(
        text = viaInstitution,
        style = MaterialTheme.typography.bodyLarge,
    )
    PrimaryButton(
        text = stringResource(R.string.VerifyIdentity_DoYouOwnAnAccount_Button_COPY),
        enabled = !isLoading,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun VerifyIdentityOption(title: String, icon: Int, isLoading: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        enabled = !isLoading,
        shape = RoundedCornerShape(CornerSize(6.dp)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        ),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 24.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterStart),
            )
            Text(
                text = title,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
@Preview
fun VerifyIdentityScreenContent_Preview() {
    EduidAppAndroidTheme {
        VerifyIdentityScreenContent(
            isLoading = false,
            errorData = null,
            dismissError = {},
            goToBankSelectionScreen = {},
            requestInstitutionLink = {},
            requestEidasLink = {},
            goToFallbackMethodScreen = {},
        )
    }
}

class ExternalAccountLinkingContract : ActivityResultContract<Intent, Intent?>() {
    override fun createIntent(context: Context, input: Intent): Intent = input

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
        Timber.d("Received callback after linking external account: ${intent?.dataString}")
        return if (resultCode == RESULT_CANCELED) {
            null
        } else {
            intent
        }
    }
}