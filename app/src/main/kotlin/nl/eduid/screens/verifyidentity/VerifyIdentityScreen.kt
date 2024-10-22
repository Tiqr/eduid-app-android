package nl.eduid.screens.verifyidentity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.screens.twofactorkey.TwoFactorKeyScreenContent
import nl.eduid.screens.twofactorkey.TwoFactorKeyScreenNoContent
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar

@Composable
fun VerifyIdentityScreen(
    viewModel: VerifyIdentityViewModel,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack
) { padding ->
    viewModel.uiState.errorData?.let { errorData ->
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = viewModel::dismissError
        )
    }
    VerifyIdentityScreenContent(
        isLoading = viewModel.uiState.isLoading,
        padding = padding
    )
}

@Composable
fun VerifyIdentityScreenContent(
    isLoading: Boolean = false,
    padding: PaddingValues = PaddingValues(),
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .systemBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
) {
    if (isLoading) {
        CircularProgressIndicator()
    } else {

    }
}