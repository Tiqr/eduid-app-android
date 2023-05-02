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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        viewModel.uiState.errorData?.let { errorData ->
            val context = LocalContext.current
            AlertDialogWithSingleButton(
                title = errorData.title(context),
                explanation = errorData.message(context),
                buttonLabel = stringResource(R.string.button_ok),
                onDismiss = viewModel::clearErrorData
            )
        }

        if (viewModel.uiState.deleteService != null) {
            DeleteServiceContent(
                providerName = viewModel.uiState.deleteService?.providerName.orEmpty(),
                inProgress = viewModel.uiState.isLoading,
                removeService = { viewModel.removeService(viewModel.uiState.deleteService?.serviceProviderEntityId) },
                goBack = viewModel::cancelDeleteService
            )
        } else {
            DataAndActivityScreenContent(
                data = viewModel.uiState.data,
                isLoading = viewModel.uiState.isLoading
            ) { viewModel.goToDeleteService(it) }
        }
    }
}

@Composable
fun DataAndActivityScreenContent(
    data: List<ServiceProvider>,
    isLoading: Boolean = false,
    goToConfirmDeleteService: (ServiceProvider) -> Unit = {},
) = Column(
    verticalArrangement = Arrangement.Bottom,
    modifier = Modifier.verticalScroll(rememberScrollState())
) {
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