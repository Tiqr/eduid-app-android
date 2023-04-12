package nl.eduid.screens.dataactivity

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
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.InfoTab
import nl.eduid.ui.getDateTimeString
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun DataAndActivityScreen(
    viewModel: DataAndActivityViewModel,
    goToConfirmDeleteService: (Int) -> Unit,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack,
) {
    val uiState by viewModel.uiState.observeAsState(UiState())
    DataAndActivityScreenContent(
        dataAndActivity = uiState.data,
        isLoading = uiState.isLoading,
        errorData = uiState.errorData,
        dismissError = viewModel::clearErrorData,
        goToConfirmDeleteService = goToConfirmDeleteService,
    )
}

@Composable
fun DataAndActivityScreenContent(
    dataAndActivity: DataAndActivityData,
    isLoading: Boolean = false,
    errorData: ErrorData? = null,
    dismissError: () -> Unit = {},
    goToConfirmDeleteService: (Int) -> Unit = {},
) = Column(
    verticalArrangement = Arrangement.Bottom,
    modifier = Modifier
        .verticalScroll(rememberScrollState())
) {
    if (errorData != null) {
        AlertDialogWithSingleButton(
            title = errorData.title,
            explanation = errorData.message,
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = dismissError
        )
    }
    Spacer(Modifier.height(36.dp))
    Text(
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Start,
            color = ButtonGreen
        ),
        text = stringResource(R.string.data_info_title),
        modifier = Modifier
            .fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
        text = stringResource(R.string.data_info_subtitle),
        modifier = Modifier
            .fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    if (isLoading) {
        Spacer(Modifier.height(24.dp))
        CircularProgressIndicator(
            modifier = Modifier
                .height(80.dp)
                .width(80.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
    } else {
        dataAndActivity.providerList?.forEachIndexed { index, provider ->
            InfoTab(
                startIconLargeUrl = provider.providerLogoUrl,
                title = provider.providerName,
                subtitle = stringResource(
                    R.string.data_info_on_date,
                    provider.firstLoginStamp.getDateTimeString()
                ),
                onClick = { },
                onDeleteButtonClicked = { goToConfirmDeleteService(index) },
                endIcon = R.drawable.chevron_down,
                serviceProviderInfo = provider,
            )
        }
    }
}


@Preview
@Composable
private fun PreviewDataAndActivityScreenContent() = EduidAppAndroidTheme {
    DataAndActivityScreenContent(
        dataAndActivity = DataAndActivityData(),
    )
}