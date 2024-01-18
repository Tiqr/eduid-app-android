package nl.eduid.screens.homepage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.launch
import nl.eduid.R
import nl.eduid.screens.info.AboutInfo
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.AlertDialogWithTwoButton
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.SmallActionGray
import org.tiqr.data.model.EnrollmentChallenge
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageNoAccountContent(
    viewModel: HomePageViewModel,
    onGoToScan: () -> Unit = {},
    onGoToRequestEduId: () -> Unit = {},
    onGoToSignIn: () -> Unit = {},
    onGoToRegistrationPinSetup: (EnrollmentChallenge) -> Unit = {},
    onGoToConfirmDeactivation: (String) -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { showBottomSheet = true },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            tint = SmallActionGray,
                            contentDescription = stringResource(id = R.string.About_Info_COPY),
                            modifier = Modifier.size(width = 46.dp, height = 46.dp)
                        )
                    }
                },
                title = {},
            )
        },
    ) { paddingValues ->
        val isAuthorizedForDataAccess by viewModel.isAuthorizedForDataAccess.observeAsState(false)
        var waitingForVmEvent by rememberSaveable { mutableStateOf(false) }
        var wasOAuthTriggered by rememberSaveable { mutableStateOf(false) }
        val waitToComplete by remember { derivedStateOf { viewModel.uiState.inProgress || waitingForVmEvent } }

        if (waitingForVmEvent) {
            viewModel.uiState.errorData?.let { errorData ->
                val context = LocalContext.current
                AlertDialogWithSingleButton(title = errorData.title(context),
                    explanation = errorData.message(context),
                    buttonLabel = stringResource(R.string.Button_OK_COPY),
                    onDismiss = {
                        waitingForVmEvent = false
                        viewModel.dismissError()
                    })
            }
            viewModel.uiState.preEnrollCheck?.let { preEnrollCheck ->
                when (preEnrollCheck) {
                    PreEnrollCheck.AlreadyCompleted -> AlertDialogWithSingleButton(title = stringResource(
                        R.string.Security_Tiqr_EnrollmentCompleted_Title_COPY
                    ),
                        explanation = stringResource(R.string.Security_Tiqr_EnrollmentCompleted_Description_COPY),
                        buttonLabel = stringResource(R.string.Button_OK_COPY),
                        onDismiss = {
                            waitingForVmEvent = false
                            viewModel.clearPreEnrollCheck()
                        })

                    PreEnrollCheck.DeactivateExisting -> AlertDialogWithTwoButton(title = stringResource(
                        R.string.Security_Tiqr_AlreadyEnrolled_Title_COPY
                    ),
                        explanation = stringResource(id = R.string.Security_Tiqr_AlreadyEnrolled_Description_COPY),
                        dismissButtonLabel = stringResource(R.string.Modal_Cancel_COPY),
                        confirmButtonLabel = stringResource(R.string.Security_Tiqr_Deactivate_COPY),
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
                        R.string.Security_Tiqr_EnrollmentIncomplete_Title_COPY
                    ),
                        explanation = stringResource(R.string.Security_Tiqr_EnrollmentIncomplete_Description_COPY),
                        buttonLabel = stringResource(R.string.Button_OK_COPY),
                        onDismiss = {
                            waitingForVmEvent = false
                            viewModel.clearPreEnrollCheck()
                        })

                    PreEnrollCheck.MissingAccount -> AlertDialogWithSingleButton(title = stringResource(
                        R.string.Security_Tiqr_MissingAccountDetails_Title_COPY
                    ),
                        explanation = stringResource(R.string.Security_Tiqr_MissingAccountDetails_Description_COPY),
                        buttonLabel = stringResource(R.string.Button_OK_COPY),
                        onDismiss = {
                            waitingForVmEvent = false
                            viewModel.clearPreEnrollCheck()
                        })
                }
            }

            viewModel.uiState.deactivateFor?.let {
                val currentConfirmDeactivation by rememberUpdatedState(onGoToConfirmDeactivation)
                LaunchedEffect(viewModel.uiState) {
                    currentConfirmDeactivation(it.phoneNumber)
                    viewModel.clearDeactivation()
                    waitingForVmEvent = false
                }
            }

            if (isAuthorizedForDataAccess && wasOAuthTriggered) {
                LaunchedEffect(viewModel.uiState) {
                    if (viewModel.uiState.canAutomaticallyTriggerEnroll()) {
                        Timber.e("Automatically starting enrollment now")
                        viewModel.startEnrollmentAfterSignIn()
                        wasOAuthTriggered = false
                    }
                }
            }
            if (viewModel.uiState.haveValidChallenge()) {
                val currentGoToRegistrationPinSetup by rememberUpdatedState(
                    onGoToRegistrationPinSetup
                )
                LaunchedEffect(viewModel.uiState) {
                    currentGoToRegistrationPinSetup(viewModel.uiState.currentChallenge as EnrollmentChallenge)
                    viewModel.clearCurrentChallenge()
                    waitingForVmEvent = false
                }
            }
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                val scope = rememberCoroutineScope()
                AboutInfo(
                    onClose = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    },
                )
            }
        }
        ConstraintLayout(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
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
                painter = painterResource(id = R.drawable.ic_correct_logo),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(64.dp)
                    .constrainAs(eduIdLogo) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
            )

            Text(
                text = stringResource(R.string.CreateEduID_LandingPage_MainText_COPY),
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
                    text = stringResource(R.string.CreateEduID_LandingPage_SignInButton_COPY),
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
                    text = stringResource(R.string.CreateEduID_LandingPage_ScanQrButton_COPY),
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
                    text = stringResource(R.string.CreateEduID_LandingPage_NoEduIdButton_COPY),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = ButtonGreen, fontWeight = FontWeight.SemiBold
                    ),
                )
            }
        }
    }
}