package nl.eduid.screens.dataactivity

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.DeleteServiceDialog
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.LoginInfoCard
import nl.eduid.ui.RevokeTokenDialog
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun DataAndActivityScreen(viewModel: DataAndActivityViewModel, goBack: () -> Unit) {
    BackHandler { viewModel.handleBackNavigation(goBack) }
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    EduIdTopAppBar(
        onBackClicked = dispatcher::onBackPressed,
    ) { paddingValues ->
        viewModel.uiState.errorData?.let { errorData ->
            val context = LocalContext.current
            AlertDialogWithSingleButton(
                title = errorData.title(context),
                explanation = errorData.message(context),
                buttonLabel = stringResource(R.string.Button_OK_COPY),
                onDismiss = viewModel::clearErrorData,
            )
        }
        if (viewModel.uiState.deleteService != null) {
            DeleteServiceDialog(
                service =
                    viewModel.uiState.deleteService
                        ?.providerName
                        .orEmpty(),
                onDismiss = viewModel::cancelDeleteService,
            ) { viewModel.removeService(viewModel.uiState.deleteService?.serviceProviderEntityId) }
        }

        if (viewModel.uiState.revokeToken != null) {
            RevokeTokenDialog(
                token = viewModel.uiState.revokeToken
                    ?.forProviderName
                    .orEmpty(),
                onDismiss = viewModel::cancelRevokeToken,
            ) { viewModel.revokeToken() }
        }

        DataAndActivityScreenContent(
            data = viewModel.uiState.data,
            isLoading = viewModel.uiState.isLoading,
            paddingValues = paddingValues,
            goToConfirmDeleteService = viewModel::showDeleteServiceDialog,
            goToConfirmDeleteToken = viewModel::showDeleteTokenDialog,
        )
    }
}

@Composable
fun DataAndActivityScreenContent(
    data: List<ServiceProvider>,
    isLoading: Boolean = false,
    paddingValues: PaddingValues = PaddingValues(),
    goToConfirmDeleteService: (ServiceProvider) -> Unit = {},
    goToConfirmDeleteToken: (ScopeAccessGrant) -> Unit = {},
) = Column(
    modifier =
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .systemBarsPadding()
            .padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
) {
    Text(
        style =
            MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSecondary,
            ),
        text = stringResource(R.string.DataActivity_Title_COPY),
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
    )
    Text(
        style = MaterialTheme.typography.bodyLarge,
        text = stringResource(R.string.DataActivity_Info_COPY),
    )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_key_data_activity_access),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(R.string.DataActivity_ExplainIcon_COPY),
        )
    }
    if (isLoading) {
        CircularProgressIndicator(
            modifier =
                Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .align(alignment = Alignment.CenterHorizontally),
        )
    } else {
        data.forEach { provider ->
            LoginInfoCard(
                title = provider.providerName.orEmpty(),
                onDeleteServiceClicked = { goToConfirmDeleteService(provider) },
                onRevokeTokenClicked = goToConfirmDeleteToken,
                serviceProviderInfo = provider,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDataAndActivityScreenContent() = EduidAppAndroidTheme {
    DataAndActivityScreenContent(
        data =
            listOf(
                ServiceProvider(
                    providerName = "Service Provider Name",
                    createdStamp = 0L,
                    firstLoginStamp = 0L,
                    uniqueId = "uniqueId",
                    serviceProviderEntityId = "serviceprovideridurl",
                    providerLogoUrl = "dummyImageUrl",
                ),
            ),
    )
}