package nl.eduid.screens.biometric

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun EnableBiometricScreen(
    viewModel: EnableBiometricViewModel,
    goToNext: (Boolean) -> Unit,
    goToHome: () -> Unit,
) {
    BackHandler {
        viewModel.stopOfferBiometric()
        goToHome()
    }
    var waitingForVmEvent by rememberSaveable { mutableStateOf(false) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    if (waitingForVmEvent) {
        val currentGoToNextStep by rememberUpdatedState(goToNext)
        LaunchedEffect(lifecycle) {
            snapshotFlow { viewModel.shouldAskForRecovery }.distinctUntilChanged().filterNotNull()
                .flowWithLifecycle(lifecycle).collect {
                    waitingForVmEvent = false
                    currentGoToNextStep(it)
                }
        }
    }

    EnableBiometricContent(
        enable = {
            viewModel.upgradeBiometric()
            waitingForVmEvent = true
        },
    ) {
        viewModel.stopOfferBiometric()
        waitingForVmEvent = true
    }
}

@Composable
fun EnableBiometricContent(
    enable: () -> Unit = {},
    skip: () -> Unit = {},
) {
    //If & when the user navigates back from this screen, we'll treat it as a Skip This action.
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher
    EduIdTopAppBar(
        onBackClicked = dispatcher::onBackPressed,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f, false)
            ) {
                Text(
                    text = stringResource(R.string.PinAndBioMetrics_BiometricsApproval_COPY),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = stringResource(R.string.PinAndBioMetrics_BiometricsExplain_COPY),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_biometric_background),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 50.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_biometric_background_front),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,

                        alignment = Alignment.CenterEnd,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            ) {
                PrimaryButton(
                    text = stringResource(R.string.PinAndBioMetrics_SetupBiometrics_COPY),
                    onClick = {
                        enable()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))
                SecondaryButton(
                    text = stringResource(R.string.PinAndBioMetrics_Skip_COPY), onClick = {
                        skip()
                    }, modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Preview(locale = "nl", showSystemUi = true)
@Composable
private fun Preview_EnableBiometricContent() {
    EduidAppAndroidTheme {
        EnableBiometricContent()
    }
}