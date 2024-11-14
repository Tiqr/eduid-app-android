package nl.eduid.screens.homepage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import nl.eduid.R
import nl.eduid.screens.info.AboutInfo
import nl.eduid.ui.AlertDialogWithTwoButton
import nl.eduid.ui.PrimaryButtonWithIcon
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.SmallActionGray
import nl.eduid.ui.theme.SplashScreenBackgroundColor
import nl.eduid.ui.theme.ColorScale_Gray_Black
import nl.eduid.ui.theme.ColorMain_Green_400
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageWithAccountContent(
    viewModel: HomePageViewModel,
    onActivityClicked: () -> Unit = {},
    onPersonalInfoClicked: () -> Unit = {},
    onSecurityClicked: () -> Unit = {},
    onScanForAuthorization: () -> Unit = {},
    launchOAuth: () -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        val context = LocalContext.current
        val notificationPermission = Manifest.permission.POST_NOTIFICATIONS
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Timber.i("Notification permission granted")
            } else {
                Timber.d("Showing notification permission dialog")
            }
        }
        LaunchedEffect(lifecycle){
            if (ContextCompat.checkSelfPermission(context, notificationPermission) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(notificationPermission)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(top = 42.dp, start = 26.dp, end = 26.dp),
                navigationIcon = {
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            tint = SmallActionGray,
                            contentDescription = "",
                            modifier = Modifier.size(width = 46.dp, height = 46.dp)
                        )
                    }
                },
                title = {
                    Image(
                        painter = painterResource(R.drawable.ic_correct_logo),
                        contentDescription = stringResource(id = R.string.About_Info_COPY),
                        modifier = Modifier.size(width = 122.dp, height = 46.dp),
                        alignment = Alignment.Center
                    )
                },
            )
        },
    ) { paddingValues ->
        val isAuthorizedForDataAccess by viewModel.isAuthorizedForDataAccess.observeAsState(false)
        viewModel.uiState.promptForAuth?.let {
            AlertDialogWithTwoButton(title = stringResource(R.string.PromptForOAuth_Title_COPY),
                explanation = stringResource(id = R.string.PromptForOAuth_Description_COPY),
                dismissButtonLabel = stringResource(R.string.Button_Cancel_COPY),
                confirmButtonLabel = stringResource(R.string.PinAndBioMetrics_SignIn_COPY),
                onDismiss = viewModel::clearPromptForAuthTrigger,
                onConfirm = {
                    viewModel.clearPromptForAuthTrigger()
                    launchOAuth()
                })
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                contentWindowInsets = { WindowInsets(bottom = 0) },
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

        AccountContent(
            paddingValues = paddingValues,
            isAuthorizedForDataAccess = isAuthorizedForDataAccess,
            onScanForAuthorization = onScanForAuthorization,
            onPersonalInfoClicked = onPersonalInfoClicked,
            promptAuthorization = viewModel::triggerPromptForAuth,
            onSecurityClicked = onSecurityClicked,
            onActivityClicked = onActivityClicked
        )
    }
}

@Composable
private fun AccountContent(
    paddingValues: PaddingValues,
    isAuthorizedForDataAccess: Boolean,
    onScanForAuthorization: () -> Unit = {},
    onPersonalInfoClicked: () -> Unit = {},
    promptAuthorization: () -> Unit = {},
    onSecurityClicked: () -> Unit = {},
    onActivityClicked: () -> Unit = {},
) {
    val bottomPadding = paddingValues.calculateBottomPadding()
    val paddingValuesWithoutBottom = PaddingValues(
        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
        top = paddingValues.calculateTopPadding(),
        bottom = 0.dp
    )
    ConstraintLayout(
        modifier = Modifier
            .padding(paddingValuesWithoutBottom)
            .fillMaxSize()
    ) {
        val (title, bottomColumn) = createRefs()

        Text(
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = ColorMain_Green_400)) {
                    append(stringResource(R.string.HomeView_MainText_FirstPart_COPY))
                }
                append("\n")
                withStyle(style = SpanStyle(color = ColorScale_Gray_Black)) {
                    append(stringResource(R.string.HomeView_MainText_SecondPart_COPY))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    bottom.linkTo(bottomColumn.top)
                },
        )

        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.constrainAs(bottomColumn) {
                bottom.linkTo(parent.bottom)
            },
        ) {
            Image(
                painter = painterResource(id = R.drawable.medidate_image),
                contentDescription = "",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 1.dp)
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .background(SplashScreenBackgroundColor)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PrimaryButtonWithIcon(
                        text = stringResource(R.string.HomeView_ScanQRButton_COPY), onClick = {
                            onScanForAuthorization()
                        }, icon = R.drawable.homepage_scan_icon, modifier = Modifier.weight(1f)
                    )
                    PrimaryButtonWithIcon(
                        text = stringResource(R.string.HomeView_PersonalInfoButton_COPY),
                        onClick = {
                            if (isAuthorizedForDataAccess) {
                                onPersonalInfoClicked()
                            } else {
                                promptAuthorization()
                            }
                        },
                        icon = R.drawable.homepage_info_icon,
                        modifier = Modifier.weight(1f)
                    )

                    PrimaryButtonWithIcon(
                        text = stringResource(R.string.HomeView_SecurityButton_COPY),
                        onClick = {
                            if (isAuthorizedForDataAccess) {
                                onSecurityClicked()
                            } else {
                                promptAuthorization()
                            }
                        },
                        icon = R.drawable.homepage_security_icon,
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButtonWithIcon(
                        text = stringResource(R.string.HomeView_ActivityButton_COPY),
                        onClick = {
                            if (isAuthorizedForDataAccess) {
                                onActivityClicked()
                            } else {
                                promptAuthorization()
                            }
                        },
                        icon = R.drawable.homepage_activity_icon,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(40.dp + bottomPadding))
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHomePageScreen() = EduidAppAndroidTheme {
    AccountContent(paddingValues = PaddingValues(), isAuthorizedForDataAccess = true)
}
