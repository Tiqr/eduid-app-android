package nl.eduid.screens.homepage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.AlertDialogWithTwoButton
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.ButtonGreen
import org.tiqr.data.model.EnrollmentChallenge
import timber.log.Timber
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageNoAccountContent(
    viewModel: HomePageViewModel,
    onGoToScan: () -> Unit = {},
    onGoToRequestEduId: () -> Unit = {},
    onGoToSignIn: () -> Unit = {},
    onGoToRegistrationPinSetup: (EnrollmentChallenge) -> Unit = {},
    onGoToConfirmDeactivation: (String) -> Unit,
) = Scaffold { paddingValues ->
    val isAuthorizedForDataAccess by viewModel.isAuthorizedForDataAccess.observeAsState(false)
    val uiState by viewModel.uiState.observeAsState(UiState())
    var waitingForVmEvent by rememberSaveable { mutableStateOf(false) }
    var wasOAuthTriggered by rememberSaveable { mutableStateOf(false) }
    val waitToComplete by remember { derivedStateOf { uiState.inProgress || waitingForVmEvent } }

    if (waitingForVmEvent) {
        uiState.errorData?.let { errorData ->
            AlertDialogWithSingleButton(title = errorData.title,
                explanation = errorData.message,
                buttonLabel = stringResource(R.string.button_ok),
                onDismiss = {
                    waitingForVmEvent = false
                    viewModel.dismissError()
                })
        }
        uiState.preEnrollCheck?.let { preEnrollCheck ->
            when (preEnrollCheck) {
                PreEnrollCheck.AlreadyCompleted -> AlertDialogWithSingleButton(title = stringResource(
                    R.string.preenroll_check_completed_title
                ),
                    explanation = stringResource(R.string.preenroll_check_completed_explanation),
                    buttonLabel = stringResource(R.string.button_ok),
                    onDismiss = {
                        waitingForVmEvent = false
                        viewModel.clearPreEnrollCheck()
                    })

                PreEnrollCheck.DeactivateExisting -> AlertDialogWithTwoButton(title = stringResource(
                    R.string.preenroll_check_blocked_title
                ),
                    explanation = stringResource(id = R.string.preenroll_check_blocked_explanation),
                    dismissButtonLabel = stringResource(R.string.preenroll_button_cancel),
                    confirmButtonLabel = stringResource(R.string.preenroll_button_deactivate),
                    onDismiss = {
                        waitingForVmEvent = false
                        viewModel.clearPreEnrollCheck()
                    },
                    onConfirm = {
                        //Continue to wait for the the VM event
                        //sent when the request for the deactivation code was requested successfully.
                        //Clearing the pre-enroll check is handled in the VM coroutine
                        viewModel.requestDeactivationCode()
                    })

                PreEnrollCheck.Incomplete -> AlertDialogWithSingleButton(title = stringResource(
                    R.string.preenroll_check_incompleted_title
                ),
                    explanation = stringResource(R.string.preenroll_check_incompleted_explanation),
                    buttonLabel = stringResource(R.string.button_ok),
                    onDismiss = {
                        waitingForVmEvent = false
                        viewModel.clearPreEnrollCheck()
                    })

                PreEnrollCheck.MissingAccount -> AlertDialogWithSingleButton(title = stringResource(
                    R.string.preenroll_check_missing_title
                ),
                    explanation = stringResource(R.string.preenroll_check_missing_explanation),
                    buttonLabel = stringResource(R.string.button_ok),
                    onDismiss = {
                        waitingForVmEvent = false
                        viewModel.clearPreEnrollCheck()
                    })
            }
        }

        uiState.deactivateFor?.let {
            val currentConfirmDeactivation by rememberUpdatedState(onGoToConfirmDeactivation)
            LaunchedEffect(viewModel.uiState) {
                currentConfirmDeactivation(it.phoneNumber)
                viewModel.clearDeactivation()
                waitingForVmEvent = false
            }
        }

        if (isAuthorizedForDataAccess && wasOAuthTriggered) {
            LaunchedEffect(uiState) {
                if (uiState.canAutomaticallyTriggerEnroll()) {
                    Timber.e("Automatically starting enrollment now")
                    viewModel.startEnrollmentAfterSignIn()
                    wasOAuthTriggered = false
                }
            }
        }
        if (uiState.haveValidChallenge()) {
            val currentGoToRegistrationPinSetup by rememberUpdatedState(onGoToRegistrationPinSetup)
            LaunchedEffect(uiState) {
                currentGoToRegistrationPinSetup(uiState.currentChallenge as EnrollmentChallenge)
                viewModel.clearCurrentChallenge()
                waitingForVmEvent = false
            }
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
                    if (isAuthorizedForDataAccess) {
                        viewModel.startEnrollmentAfterSignIn()
                    } else {
                        wasOAuthTriggered = true
                        onGoToSignIn()
                    }
                    waitingForVmEvent = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = stringResource(R.string.scan_button),
                enabled = !waitToComplete,
                onClick = onGoToScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
        }

        TextButton(
            onClick = onGoToRequestEduId,
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 48.dp)
                .padding(horizontal = 32.dp)
                .constrainAs(requestEduIdButton) {
                    bottom.linkTo(parent.bottom)
                },
            shape = RoundedCornerShape(CornerSize(6.dp)),
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