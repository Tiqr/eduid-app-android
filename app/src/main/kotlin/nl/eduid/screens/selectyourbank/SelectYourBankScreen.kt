package nl.eduid.screens.selectyourbank

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.model.VerifyIssuer
import nl.eduid.screens.verifyidentity.ExternalAccountLinkingContract
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.SvgImage
import nl.eduid.ui.annotatedStringWithBoldParts
import nl.eduid.ui.theme.AlertWarningBackground
import nl.eduid.ui.theme.ColorMain_Green_400
import nl.eduid.ui.theme.ColorSupport_Blue_400
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun SelectYourBankScreen(
    viewModel: SelectYourBankViewModel,
    goBack: () -> Unit,
) = EduIdTopAppBar(onBackClicked = goBack) { paddingValues ->

    val launcher = rememberLauncherForActivityResult(contract = ExternalAccountLinkingContract(), onResult = { _ ->
        // TODO: handle result
    })

    LaunchedEffect(viewModel.uiState.launchIntent) {
        viewModel.uiState.launchIntent?.let {
            launcher.launch(it)
            viewModel.clearLaunchIntent()
        }
    }

    SelectYourBankScreenContent(
        isLoading = viewModel.uiState.isLoading,
        errorData = viewModel.uiState.errorData,
        verifyIssuerList = viewModel.uiState.verifyIssuerList,
        dismissError = viewModel::dismissError,
        goBack = goBack,
        linkAccountWithBankId = { issuerId, callback ->
            viewModel.linkAccountWithBankId(issuerId, callback)
        },
        padding = paddingValues
    )
}


@Composable
fun SelectYourBankScreenContent(
    isLoading: Boolean,
    errorData: ErrorData?,
    verifyIssuerList: List<VerifyIssuer>,
    linkAccountWithBankId: (String, () -> Unit) -> Unit,
    dismissError: () -> Unit,
    goBack: () -> Unit,
    padding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current

    if (errorData != null) {
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = dismissError
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(
            start = padding.calculateStartPadding(LayoutDirection.Ltr),
            end = padding.calculateEndPadding(LayoutDirection.Ltr),
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding() + 48.dp
        ),
        modifier = Modifier
            .padding(start = 24.dp, end = 24.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.SelectYourBank_Title_COPY),
                style = MaterialTheme.typography.titleLarge.copy(color = ColorMain_Green_400),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = annotatedStringWithBoldParts(
                    stringResource(R.string.SelectYourBank_Subtitle_Full_COPY),
                    stringResource(R.string.SelectYourBank_Subtitle_HighlightedPart_COPY),
                ),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = buildAnnotatedString {
                    val idinLink = stringResource(R.string.SelectYourBank_IdinkLink_COPY)
                    val link = LinkAnnotation.Url(idinLink)
                    withLink(link) {
                        append(stringResource(R.string.SelectYourBank_MoreAboutIdin_COPY))
                    }
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 1.dp)
            )
            Spacer(Modifier.height(32.dp))
        }
        if (isLoading) {
            item {
                // Show loading spinner
                Spacer(Modifier.height(32.dp))
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            // Show bank list
            items(verifyIssuerList) {
                // Show bank item
                var isFetchingLink by remember { mutableStateOf(false) }
                Surface(
                    color = Color.Transparent,
                    enabled = !isFetchingLink,
                    onClick = {
                        it.id?.let { issuerId ->
                            isFetchingLink = true
                            linkAccountWithBankId(issuerId) {
                                isFetchingLink = false
                            }

                        }
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(71.dp)
                            .border(3.dp, ColorSupport_Blue_400, shape = RoundedCornerShape(6.dp))
                    ) {
                        Spacer(Modifier.width(12.dp))
                        SvgImage(
                            svgString = it.logo ?: "",
                            modifier = Modifier
                                .size(72.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = it.name ?: it.id ?: "",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = ColorSupport_Blue_400
                            ),
                            modifier = Modifier.align(Alignment.CenterVertically)
                                .weight(1f)
                        )
                        if (isFetchingLink) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp).align(Alignment.CenterVertically),
                            )
                        }
                        Spacer(Modifier.width(16.dp))

                    }
                }
                Spacer(Modifier.height(24.dp))
            }
            item {
                Surface(color = AlertWarningBackground) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_warning),
                            contentDescription = null,
                            modifier = Modifier
                                .width(24.dp)
                                .height(48.dp)
                                .padding(top = 4.dp)
                        )
                        Spacer(Modifier.size(12.dp))
                        Text(
                            text = buildAnnotatedString {
                                val full = stringResource(R.string.SelectYourBank_BankNotInList_Full_COPY)
                                val highlighted = stringResource(R.string.SelectYourBank_BankNotInList_HighlightedPart_COPY)
                                append(full.substringBefore(highlighted))
                                withLink(
                                    LinkAnnotation.Clickable(
                                        "select_other_method", linkInteractionListener = {
                                            goBack()
                                        }, styles = TextLinkStyles(
                                            style = SpanStyle(
                                                color = ColorSupport_Blue_400,
                                                textDecoration = TextDecoration.Underline
                                            )
                                        )
                                    )
                                ) {
                                    append(highlighted)
                                }
                                append(full.substringAfter(highlighted))
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun SelectYourBankScreenPreview() {
    EduidAppAndroidTheme {
        SelectYourBankScreenContent(
            isLoading = false,
            errorData = null,
            verifyIssuerList = emptyList(),
            dismissError = {},
            linkAccountWithBankId = { _, _ -> },
            goBack = {}
        )
    }
}