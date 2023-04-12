package nl.eduid.screens.dataactivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.theme.AlertRedBackground
import nl.eduid.ui.theme.ButtonRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGreen

@Composable
fun DeleteServiceScreen(
    viewModel: DataAndActivityViewModel,
    index: Int,
    goBack: () -> Unit = {},
) = EduIdTopAppBar(
    onBackClicked = goBack,
) {
    val uiState by viewModel.uiState.observeAsState(UiState())
    val provider by remember(index) {
        derivedStateOf {
            uiState.data.providerList?.get(index)
        }
    }
    DeleteServiceContent(
        providerName = provider?.providerName.orEmpty(),
        inProgress = uiState.isLoading,
        removeService = { viewModel.removeService(provider?.serviceProviderEntityId) },
        goBack = goBack
    )
}

@Composable
private fun DeleteServiceContent(
    providerName: String,
    isComplete: Unit? = null,
    inProgress: Boolean = false,
    removeService: () -> Unit = {},
    goBack: () -> Unit = {},
) {
    var isProcessing by rememberSaveable { mutableStateOf(false) }
    val owner = LocalLifecycleOwner.current
    if (isProcessing && isComplete != null) {
        val currentGoBack by rememberUpdatedState(goBack)
        LaunchedEffect(owner) {
            isProcessing = false
            currentGoBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.delete_service_confirm_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextGreen, textAlign = TextAlign.Start
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(18.dp))
            if (inProgress) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = AlertRedBackground)
            ) {
                Image(
                    painter = painterResource(R.drawable.warning_icon_red),
                    contentDescription = "",
                    modifier = Modifier.padding(12.dp)
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    text = stringResource(R.string.delete_no_undo_warning),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = stringResource(
                    R.string.delete_service_confirm_explanation, providerName
                ),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(36.dp))
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
        ) {
            SecondaryButton(
                modifier = Modifier.widthIn(min = 140.dp),
                text = stringResource(R.string.button_cancel),
                onClick = goBack,
            )
            PrimaryButton(
                modifier = Modifier.widthIn(min = 140.dp),
                text = stringResource(R.string.button_confirm),
                onClick = {
                    isProcessing = true
                    removeService()
                },
                buttonBackgroundColor = ButtonRed,
                buttonTextColor = Color.White,
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Preview()
@Composable
private fun PreviewDeleteServiceScreen() {
    EduidAppAndroidTheme {
        DeleteServiceContent(
            providerName = "OpenConext Profile",
        )
    }
}