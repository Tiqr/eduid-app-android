package nl.eduid.screens.removeapp

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.DestructiveConfirmationContent
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun RemoveMobileAppScreen(
    viewModel: RemoveMobileAppViewModel,
    goBack: () -> Unit,
    goToConfirmCode: (String) -> Unit,
) = EduIdTopAppBar(onBackClicked = goBack) { padding ->
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentGoToConfirmCode by rememberUpdatedState(goToConfirmCode)

    uiState.errorData?.let { errorData ->
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = viewModel::dismissError,
        )
    }

    LaunchedEffect(uiState.codeSentTo) {
        uiState.codeSentTo?.let { phoneNumber ->
            viewModel.clearCodeSent()
            currentGoToConfirmCode(phoneNumber)
        }
    }

    RemoveMobileAppContent(
        inProgress = uiState.inProgress,
        padding = padding,
        onCancel = goBack,
        onConfirm = viewModel::requestDeactivationCode,
    )
}

@Composable
private fun RemoveMobileAppContent(
    inProgress: Boolean,
    padding: PaddingValues = PaddingValues(),
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
) = DestructiveConfirmationContent(
    title = stringResource(R.string.ConfirmRemoveMobileApp_Title_COPY),
    warning = stringResource(R.string.ConfirmRemoveMobileApp_Subtitle_COPY),
    description = stringResource(R.string.ConfirmRemoveMobileApp_Description_COPY),
    cancelLabel = stringResource(R.string.ConfirmRemoveMobileApp_Button_Cancel_COPY),
    confirmLabel = stringResource(R.string.ConfirmRemoveMobileApp_Button_Confirm_COPY),
    inProgress = inProgress,
    padding = padding,
    onCancel = onCancel,
    onConfirm = onConfirm,
)

@Preview(showBackground = true)
@Composable
private fun Preview_RemoveMobileAppContent() = EduidAppAndroidTheme {
    RemoveMobileAppContent(inProgress = false)
}

@Preview(showBackground = true)
@Composable
private fun Preview_RemoveMobileAppContent_InProgress() = EduidAppAndroidTheme {
    RemoveMobileAppContent(inProgress = true)
}
