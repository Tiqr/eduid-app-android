package nl.eduid.screens.deletepasskey

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
fun DeletePasskeyScreen(
    viewModel: DeletePasskeyViewModel,
    goBack: () -> Unit,
    onDeleteDone: () -> Unit,
) = EduIdTopAppBar(onBackClicked = goBack) { padding ->
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnDeleteDone by rememberUpdatedState(onDeleteDone)

    uiState.errorData?.let { errorData ->
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = viewModel::dismissError,
        )
    }

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted != null) {
            viewModel.clearCompleted()
            currentOnDeleteDone()
        }
    }

    DeletePasskeyContent(
        passkeyName = viewModel.passkeyName,
        inProgress = uiState.inProgress,
        padding = padding,
        onCancel = goBack,
        onConfirm = viewModel::deletePasskey,
    )
}

@Composable
private fun DeletePasskeyContent(
    passkeyName: String,
    inProgress: Boolean,
    padding: PaddingValues = PaddingValues(),
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
) = DestructiveConfirmationContent(
    title = stringResource(R.string.ConfirmDeletePasskey_Title_COPY),
    warning = stringResource(R.string.ConfirmDeletePasskey_Subtitle_COPY),
    description = stringResource(R.string.ConfirmDeletePasskey_Description_COPY, passkeyName),
    cancelLabel = stringResource(R.string.ConfirmDeletePasskey_Button_Cancel_COPY),
    confirmLabel = stringResource(R.string.ConfirmDeletePasskey_Button_Confirm_COPY),
    inProgress = inProgress,
    padding = padding,
    onCancel = onCancel,
    onConfirm = onConfirm,
)

@Preview(showBackground = true)
@Composable
private fun Preview_DeletePasskeyContent() = EduidAppAndroidTheme {
    DeletePasskeyContent(passkeyName = "My laptop", inProgress = false)
}

@Preview(showBackground = true)
@Composable
private fun Preview_DeletePasskeyContent_InProgress() = EduidAppAndroidTheme {
    DeletePasskeyContent(passkeyName = "My laptop", inProgress = true)
}
