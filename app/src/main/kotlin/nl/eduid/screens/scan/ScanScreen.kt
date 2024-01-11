package nl.eduid.screens.scan

import android.Manifest
import android.view.KeyEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithTwoButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import org.tiqr.data.model.Challenge
import org.tiqr.data.scan.ScanComponent
import androidx.camera.core.Preview as CameraPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    viewModel: StatelessScanViewModel,
    isEnrolment: Boolean,
    goBack: () -> Unit,
    goToNext: (Challenge) -> Unit,
    state: ScanState = rememberScanState(
        viewModel = viewModel,
        goBack = goBack,
        goToNext = goToNext,
    )
) = Scaffold(
    modifier = Modifier
        .systemBarsPadding()
        .onKeyEvent { keyEvent ->
            //This will only work if the screen has a focused child. Which will prompt they keyboard.
            //The other solution is to listen to key events on the MainComposeActivity but then we need to learn the currently displayed screen,
            // which is another problem. The action bar click works if & when the core exposes the functionality
            when (keyEvent.nativeKeyEvent.keyCode) {
                KeyEvent.KEYCODE_FOCUS, KeyEvent.KEYCODE_CAMERA, KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    state.toggleTorch()
                    true // Mark as handled since wse sent the broadcast because currently scanning
                }

                else -> {
                    false
                }
            }
        },
    topBar = {
        TopAppBar(
            title = {},
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.Transparent
            ),
            navigationIcon = {
                IconButton(onClick = goBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = stringResource(R.string.PinAndBioMetrics_Button_Back_COPY),
                        modifier = Modifier.size(width = 53.dp, height = 53.dp)
                    )
                }
            },
            actions = {
                Image(painter = painterResource(R.drawable.ic_flashlight),
                    contentDescription = stringResource(R.string.ScanView_Flashlight_TurnOn_COPY),
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.onPrimary, BlendMode.SrcIn
                    ),
                    modifier = Modifier
                        .size(width = 53.dp, height = 53.dp)
                        .clickable { state.toggleTorch() })
            },
        )
    },
) { paddingValues ->
    ScanContent(
        isEnrolment = isEnrolment,
        hasCamPermission = state.hasCamPermission,
        camPermissionUpdated = { state.camPermissionUpdated(it) },
        errorData = state.errorData,
        dismissErrorDialog = state::dismissErrorDialog,
        retryErrorDialog = state::retryErrorDialog,
        onScanResult = { state.onScanResult(it) },
        updateScanComponent = { state.updateScanComponent(it) },
        paddingValues = paddingValues,
        lifecycleOwner = state.lifecycleOwner
    )
}

@Composable
private fun ScanContent(
    isEnrolment: Boolean,
    hasCamPermission: Boolean,
    camPermissionUpdated: (Boolean) -> Unit,
    errorData: ErrorData?,
    dismissErrorDialog: () -> Unit,
    retryErrorDialog: () -> Unit,
    onScanResult: (String) -> Unit,
    updateScanComponent: (ScanComponent) -> Unit,
    paddingValues: PaddingValues = PaddingValues(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
                onResult = { granted ->
                    camPermissionUpdated(granted)
                })
        LaunchedEffect(key1 = true) {
            launcher.launch(Manifest.permission.CAMERA)
        }

        if (errorData != null) {
            val context = LocalContext.current
            AlertDialogWithTwoButton(
                title = errorData.title(context),
                explanation = errorData.message(context),
                dismissButtonLabel = stringResource(R.string.Button_Cancel_COPY),
                onDismiss = dismissErrorDialog,
                confirmButtonLabel = stringResource(R.string.Button_Retry_COPY),
                onConfirm = retryErrorDialog
            )
        }

        if (hasCamPermission) {
            if (LocalInspectionMode.current) {
                //Do nothing for preview mode
            } else {
                AndroidView(
                    factory = { factoryContext ->
                        val previewView = PreviewView(factoryContext)
                        val preview = CameraPreview.Builder().build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        val scanComponent = ScanComponent(
                            context = factoryContext,
                            lifecycleOwner = lifecycleOwner,
                            viewFinder = previewView,
                        ) { result ->
                            onScanResult(result)
                        }
                        updateScanComponent(scanComponent)

                        previewView
                    }, modifier = Modifier.fillMaxSize()
                )
            }
        }
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (background, camPermissionText, scanTitleText, registrationExplanation) = createRefs()
            val contentTopSpacing = createGuidelineFromTop(56.dp)
            Image(painter = painterResource(R.drawable.ic_scan_background),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(background) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    })
            if (!hasCamPermission) {
                Text(text = stringResource(R.string.Permission_Scan_COPY),
                    style = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.Center, color = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(camPermissionText) {
                            top.linkTo(contentTopSpacing)
                        })
            }
            if (!isEnrolment) {
                Text(text = stringResource(R.string.ScanView_Title_COPY),
                    style = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.Center, color = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(scanTitleText) {
                            top.linkTo(contentTopSpacing)
                        })
            }

            if (isEnrolment) {
                RegistrationExplanation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .constrainAs(registrationExplanation) {
                            bottom.linkTo(parent.bottom, margin = 10.dp)
                        })
            }

        }
    }
}

@Preview(name = "Scan for registration")
@Composable
private fun Preview_ScanScreen_Registration() {
    EduidAppAndroidTheme {
        ScanContent(isEnrolment = true,
            hasCamPermission = true,
            camPermissionUpdated = {},
            errorData = null,
            dismissErrorDialog = {},
            retryErrorDialog = {},
            onScanResult = {},
            updateScanComponent = {})
    }
}

@Preview(name = "Scan missing camera permission")
@Composable
private fun Preview_ScanScreen_MissingCamPermission() {
    EduidAppAndroidTheme {
        ScanContent(isEnrolment = true,
            hasCamPermission = false,
            camPermissionUpdated = {},
            errorData = null,
            dismissErrorDialog = {},
            retryErrorDialog = {},
            onScanResult = {},
            updateScanComponent = {})
    }
}

@Preview(name = "Scan for authentication")
@Composable
private fun Preview_ScanScreen_Authentication() {
    EduidAppAndroidTheme {
        ScanContent(isEnrolment = false,
            hasCamPermission = true,
            camPermissionUpdated = {},
            errorData = null,
            dismissErrorDialog = {},
            retryErrorDialog = {},
            onScanResult = {},
            updateScanComponent = {})
    }
}