package nl.eduid.screens.homepage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme
import org.tiqr.data.model.EnrollmentChallenge
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageNoAccountContent(
    isAuthorizedForDataAccess: Boolean,
    uiState: UiState,
    onScan: () -> Unit = {},
    onRequestEduId: () -> Unit = {},
    onSignIn: () -> Unit = {},
    onStartEnrolment: () -> Unit = {},
    goToRegistrationPinSetup: (EnrollmentChallenge) -> Unit = {},
    dismissError: () -> Unit = {},
) = Scaffold { paddingValues ->
    var waitingForVmEvent by rememberSaveable { mutableStateOf(false) }
    val waitToComplete by remember { derivedStateOf { uiState.inProgress || waitingForVmEvent } }
    val owner = LocalLifecycleOwner.current

    if (uiState.errorData != null) {
        AlertDialogWithSingleButton(
            title = uiState.errorData.title,
            explanation = uiState.errorData.message,
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = {
                dismissError()
                waitingForVmEvent = false
            }
        )
    }

    if (isAuthorizedForDataAccess && waitingForVmEvent && uiState.shouldTriggerAutomaticStartEnrollmentAfterOauth()) {
        val currentOnStartEnrolment by rememberUpdatedState(onStartEnrolment)
        LaunchedEffect(owner) {
            currentOnStartEnrolment()
        }
    }
    if (waitingForVmEvent && uiState.haveValidChallenge()) {
        val currentGoToRegistrationPinSetup by rememberUpdatedState(goToRegistrationPinSetup)
        LaunchedEffect(owner) {
            currentGoToRegistrationPinSetup(uiState.currentChallenge as EnrollmentChallenge)
            waitingForVmEvent = false
        }
    }
    ConstraintLayout(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .systemBarsPadding()
    ) {
        val (eduIdLogo, title, enrollLogo, requestEduIdButton, buttons) = createRefs()
        createVerticalChain(
            eduIdLogo,
            title,
            enrollLogo,
            buttons,
            requestEduIdButton,
            chainStyle = ChainStyle.Spread
        )

        Image(
            painter = painterResource(id = R.drawable.logo_eduid_big),
            contentDescription = "",
            modifier = Modifier
                .size(width = 150.dp, height = 59.dp)
                .constrainAs(eduIdLogo) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
        )

        Text(
            text = stringResource(R.string.enroll_screen_title),
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .constrainAs(title) {
                    top.linkTo(eduIdLogo.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
        )

        Image(
            painter = painterResource(id = R.drawable.enroll_screen_logo),
            contentDescription = "",
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(enrollLogo) {
                    top.linkTo(title.bottom)
                    bottom.linkTo(buttons.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .constrainAs(buttons) {
                    bottom.linkTo(requestEduIdButton.top)
                }
                .fillMaxWidth()) {

            if (waitToComplete) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            PrimaryButton(
                text = stringResource(R.string.enroll_screen_sign_in_button),
                enabled = !waitToComplete,
                onClick = {
                    waitingForVmEvent = true
                    if (isAuthorizedForDataAccess) {
                        onStartEnrolment()
                    } else {
                        onSignIn()
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = stringResource(R.string.scan_button),
                enabled = !waitToComplete,
                onClick = onScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
        }

        TextButton(
            onClick = onRequestEduId,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(requestEduIdButton) {
                    bottom.linkTo(parent.bottom)
                },
        ) {
            Text(
                text = stringResource(R.string.enroll_screen_request_id_button),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = ButtonGreen, fontWeight = FontWeight.SemiBold
                ),
            )
        }
    }
}

@Preview()
@Composable
private fun PreviewEnroll() {
    EduidAppAndroidTheme {
        HomePageNoAccountContent(isAuthorizedForDataAccess = false,
            uiState = UiState(),
            onScan = {},
            onRequestEduId = {}) {}
    }
}




