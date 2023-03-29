package nl.eduid.screens.biometric

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.EduIdTopAppBar
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
    //If & when the user navigates back from this screen, we'll treat it as a Skip This action.
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher
    val nextStep by viewModel.nextStep.observeAsState(initial = null)

    EduIdTopAppBar(
        onBackClicked = dispatcher::onBackPressed,
    ) {
        EnableBiometricContent(
            nextStep = nextStep,
            goToNext = goToNext,
            enable = {
                viewModel.upgradeBiometric()
            },
        ) {
            viewModel.stopOfferBiometric()
        }
    }
}

@Composable
fun EnableBiometricContent(
    nextStep: Boolean?,
    goToNext: (Boolean) -> Unit = {},
    enable: () -> Unit = {},
    skip: () -> Unit = {},
) {
    var inProgress by rememberSaveable { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    if (inProgress && nextStep != null) {
        val currentGoToNextStep by rememberUpdatedState(goToNext)
        LaunchedEffect(lifecycleOwner) {
            inProgress = false
            currentGoToNextStep(nextStep)
        }
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (title, bodySpacing, description, background, backgroundFront, buttons) = createRefs()
        val contentTopSpacing = createGuidelineFromTop(40.dp)
        val backgroundAlign = createGuidelineFromEnd(0.3f)
        Text(
            text = stringResource(R.string.biometric_enable_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(title) {
                    top.linkTo(contentTopSpacing)
                },
        )
        Spacer(modifier = Modifier
            .height(28.dp)
            .constrainAs(bodySpacing) {
                top.linkTo(title.bottom)
            })
        Text(
            text = stringResource(R.string.biometric_enable_description),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(description) {
                    top.linkTo(bodySpacing.bottom)
                },
        )
        Image(painter = painterResource(id = R.drawable.ic_biometric_background),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(vertical = 50.dp)
                .constrainAs(background) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(description.bottom)
                })
        Image(painter = painterResource(id = R.drawable.ic_biometric_background_front),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier.constrainAs(backgroundFront) {
                start.linkTo(backgroundAlign)
                end.linkTo(parent.end)
                top.linkTo(description.bottom)
            })

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(buttons) {
                    top.linkTo(background.bottom)
                    bottom.linkTo(parent.bottom, margin = 40.dp)
                }) {
            PrimaryButton(
                text = stringResource(R.string.biometric_enable_allow),
                onClick = {
                    enable()
                    inProgress = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
            Spacer(Modifier.height(24.dp))
            SecondaryButton(
                text = stringResource(R.string.biometric_enable_skip),
                onClick = {
                    skip()
                    inProgress = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
        }

    }
}


@Preview
@Composable
private fun Preview_EnableBiometricContent() {
    EduidAppAndroidTheme {
        EnableBiometricContent(nextStep = null)
    }
}