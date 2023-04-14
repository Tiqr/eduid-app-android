package nl.eduid.screens.dataactivity

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
    goBack: () -> Unit,
) {
    BackHandler { viewModel.handleBackNavigation(goBack) }
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    EduIdTopAppBar(
        onBackClicked = dispatcher::onBackPressed,
    ) {
        val uiState by viewModel.uiState.observeAsState(UiState())

        if (uiState.deleteService != null) {
            DeleteServiceContent(
                providerName = uiState.deleteService?.providerName.orEmpty(),
                inProgress = uiState.isLoading,
                removeService = { viewModel.removeService(uiState.deleteService?.serviceProviderEntityId) },
                goBack = viewModel::cancelDeleteService
            )
        } else {
            DataAndActivityScreenContent(data = uiState.data,
                isLoading = uiState.isLoading,
                errorData = uiState.errorData,
                dismissError = viewModel::clearErrorData,
                goToConfirmDeleteService = { viewModel.goToDeleteService(it) })
        }
    }
}

@Composable
fun DataAndActivityScreenContent(
    data: List<ServiceProvider>,
    isLoading: Boolean = false,
    errorData: ErrorData? = null,
    dismissError: () -> Unit = {},
    goToConfirmDeleteService: (ServiceProvider) -> Unit = {},
) = Column(
    verticalArrangement = Arrangement.Bottom,
    modifier = Modifier.verticalScroll(rememberScrollState())
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
            textAlign = TextAlign.Start, color = ButtonGreen
        ), text = stringResource(R.string.data_info_title), modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Justify,
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.data_info_subtitle),
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
        data.forEach { provider ->
            InfoTab(
                startIconLargeUrl = provider.providerLogoUrl,
                title = provider.providerName,
                subtitle = stringResource(
                    R.string.data_info_on_date, provider.firstLoginStamp.getDateTimeString()
                ),
                onClick = { },
                onDeleteButtonClicked = { goToConfirmDeleteService(provider) },
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
        data = listOf(
            ServiceProvider(
                providerName = "Service Provider Name",
                createdStamp = 0L,
                firstLoginStamp = 0L,
                uniqueId = "uniqueId",
                serviceProviderEntityId = "serviceprovideridurl",
                providerLogoUrl = "dummyImageUrl"
            )
        ),
    )
}