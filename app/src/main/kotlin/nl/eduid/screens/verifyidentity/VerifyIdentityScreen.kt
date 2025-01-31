package nl.eduid.screens.verifyidentity

import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.theme.ColorMain_Green_400
import nl.eduid.ui.theme.ColorScale_Gray_400
import nl.eduid.ui.theme.ColorScale_Gray_500
import nl.eduid.ui.theme.ColorSupport_Blue_400
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.InfoTabDarkFill
import timber.log.Timber


@Composable
fun VerifyIdentityScreen(
    viewModel: VerifyIdentityViewModel,
    goToBankSelectionScreen: () -> Unit,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack
) { padding ->
    val launcher = rememberLauncherForActivityResult(contract = ExternalAccountLinkingContract(), onResult = { _ ->
        /**We don't have to explicitly handle the result intent. The deep linking will
         * automatically open the [AccountLinkedScreen()] and ensure the backstack is correct.*/
    })

    LaunchedEffect(viewModel.uiState.launchIntent) {
        viewModel.uiState.launchIntent?.let { intent ->
            launcher.launch(intent)
            viewModel.clearLaunchIntent()
        }
    }

    VerifyIdentityScreenContent(
        isLoading = viewModel.uiState.isLoading,
        isLinkedAccount = viewModel.isLinkedAccount,
        fallbackMethodEnabled = viewModel.fallbackMethodEnabled,
        moreOptionsExpanded = viewModel.uiState.moreOptionsExpanded,
        errorData = viewModel.uiState.errorData,
        dismissError = viewModel::dismissError,
        goToBankSelectionScreen = goToBankSelectionScreen,
        requestInstitutionLink = viewModel::requestInstitutionLink,
        requestEidasLink = viewModel::requestEidasLink,
        openSupportUrl = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            launcher.launch(intent)
        },
        expandMoreOptions = viewModel::expandMoreOptions,
        padding = padding
    )
}

@Composable
fun VerifyIdentityScreenContent(
    isLoading: Boolean,
    isLinkedAccount: Boolean,
    fallbackMethodEnabled: Boolean,
    moreOptionsExpanded: Boolean,
    errorData: ErrorData?,
    dismissError: () -> Unit,
    goToBankSelectionScreen: () -> Unit,
    requestInstitutionLink: () -> Unit,
    requestEidasLink: () -> Unit,
    expandMoreOptions: () -> Unit,
    openSupportUrl: (String) -> Unit,
    padding: PaddingValues = PaddingValues(),
) {
    val supportUrl = stringResource(R.string.VerifyIdentity_VisitSupport_Link_COPY)
    val context = LocalContext.current

    if (errorData != null) {
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = dismissError
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
        ) {
            if (isLinkedAccount) {
                Text(
                    text = stringResource(R.string.VerifyIdentity_TitleHasInternalLink_COPY),
                    style = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 16.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.VerifyIdentity_Title_FirstLine_COPY),
                    style = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 16.dp)
                )
                Text(
                    text = stringResource(R.string.VerifyIdentity_Title_SecondLine_COPY),
                    style = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 6.dp)

                )
            }
            Spacer(Modifier.height(16.dp))
            val subtitle = if (isLinkedAccount) {
                stringResource(R.string.VerifyIdentity_SubtitleHasInternalLink_COPY)
            } else {
                stringResource(R.string.VerifyIdentity_Subtitle_COPY)
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            val instituteButtonTitle = if (isLinkedAccount) {
                stringResource(R.string.VerifyIdentity_VerifyViaDutchInstitution_TitleHasInternalLink_COPY)
            } else {
                stringResource(R.string.VerifyIdentity_VerifyViaDutchInstitution_Title_COPY)
            }
            VerifyIdentityControl(
                title = instituteButtonTitle,
                icon = painterResource(id = R.drawable.ic_verify_institution),
                buttonTitle = stringResource(R.string.VerifyIdentity_VerifyViaDutchInstitution_Button_COPY),
                isLoading = isLoading,
                onClick = { requestInstitutionLink() }
            )
            if (moreOptionsExpanded) {
                Spacer(Modifier.height(20.dp))
                VerifyIdentityControl(
                    title = stringResource(R.string.VerifyIdentity_VerifyWithBankApp_Title_COPY),
                    icon = painterResource(id = R.drawable.ic_verify_bank),
                    buttonTitle = stringResource(R.string.VerifyIdentity_VerifyWithBankApp_Button_COPY),
                    isLoading = isLoading,
                    buttonIcon = painterResource(id = R.drawable.ic_idin),
                    onClick = { goToBankSelectionScreen() }
                )
                Spacer(Modifier.height(20.dp))
                VerifyIdentityControl(
                    title = stringResource(R.string.VerifyIdentity_VerifyWithAEuropianId_Title_COPY),
                    icon = painterResource(id = R.drawable.ic_verify_id),
                    buttonTitle = stringResource(R.string.VerifyIdentity_VerifyWithAEuropianId_Button_COPY),
                    isLoading = isLoading,
                    buttonIcon = painterResource(id = R.drawable.ic_eidas),
                    onClick = { requestEidasLink() }
                )
                Spacer(Modifier.height(24.dp))
                if (!fallbackMethodEnabled) {
                    val fullText = stringResource(R.string.VerifyIdentity_VisitSupport_Full_COPY)
                    val linkedPart = stringResource(R.string.VerifyIdentity_VisitSupport_HighlightedPart_COPY)
                    val supportLinkText = buildAnnotatedString {
                        val partBefore = fullText.split(linkedPart).first()
                        val partAfter = fullText.split(linkedPart).last()
                        append(partBefore)
                        withLink(
                            link = LinkAnnotation.Clickable(
                                tag = "support_link",
                                linkInteractionListener = {
                                    openSupportUrl(supportUrl)
                                },
                                styles = TextLinkStyles(
                                    style = SpanStyle(color = ColorSupport_Blue_400)
                                )
                            ),
                        ) {
                            append(linkedPart)
                        }
                        append(partAfter)
                    }
                    Text(
                        text = supportLinkText,
                        style = MaterialTheme.typography.bodyLarge.copy(color = ColorScale_Gray_500)
                    )
                }
            } else if (!isLinkedAccount) {
                Spacer(Modifier.height(20.dp))
                OutlinedButton(
                    onClick = expandMoreOptions,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(width = 1.dp, color = ColorScale_Gray_400),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.VerifyIdentity_OtherOptions_COPY),
                        style = MaterialTheme.typography.bodyLarge.copy(color = ColorScale_Gray_500)
                    )
                }
            }
        }
        if (fallbackMethodEnabled && !isLinkedAccount && moreOptionsExpanded) {
            Box(
                Modifier.background(InfoTabDarkFill)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                SecondaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.VerifyIdentity_ICantUseTheseMethods_COPY),
                    onClick = { openSupportUrl(supportUrl) }
                )
            }
        }
    }

}

@Composable
fun VerifyIdentityControl(
    title: String,
    icon: Painter,
    buttonTitle: String,
    isLoading: Boolean,
    buttonIcon: Painter? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .border(1.dp, ColorScale_Gray_400, shape = RoundedCornerShape(6.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            OutlinedButton (
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = ColorMain_Green_400,
                    disabledContainerColor = ColorMain_Green_400.copy(alpha = 0.5f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp),
                onClick = { onClick() },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
                    .height(48.dp)
            ) {
                if (buttonIcon != null) {
                    Image(
                        painter = buttonIcon,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = buttonTitle,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).offset(x = if (buttonIcon == null) 0.dp else (-15).dp)
                )
            }
            if (isLoading) {
                CircularProgressIndicator(
                    color = ColorMain_Green_400,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(36.dp)
                        .padding(end = 12.dp, top = 8.dp)
                )
            }
        }
    }
}

@Composable
@Preview
fun VerifyIdentityScreenContent_Preview() {
    EduidAppAndroidTheme {
        VerifyIdentityScreenContent(
            isLoading = false,
            isLinkedAccount = false,
            fallbackMethodEnabled = true,
            moreOptionsExpanded = true,
            errorData = null,
            dismissError = {},
            goToBankSelectionScreen = {},
            requestInstitutionLink = {},
            requestEidasLink = {},
            openSupportUrl = {},
            expandMoreOptions = {}
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