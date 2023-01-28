package nl.eduid.screens.scan

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.tiqr.core.scan.ScanComponent
import org.tiqr.data.model.AuthenticationChallenge
import org.tiqr.data.model.ChallengeParseResult
import org.tiqr.data.model.EnrollmentChallenge
import org.tiqr.data.viewmodel.ScanViewModel
import java.util.*


@Composable
fun rememberScanState(
    viewModel: ScanViewModel,
    goBack: () -> Unit,
    goToEnroll: (EnrollmentChallenge) -> Unit,
    goToAuthentication: (AuthenticationChallenge) -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): ScanState {
    val currentGoBack by rememberUpdatedState(newValue = goBack)
    val currentGoToEnroll by rememberUpdatedState(newValue = goToEnroll)
    val currentGoToAuthentication by rememberUpdatedState(newValue = goToAuthentication)

    return remember(viewModel, lifecycleOwner, context, coroutineScope) {
        ScanState(
            viewModel = viewModel,
            currentGoBack = currentGoBack,
            lifecycleOwner = lifecycleOwner,
            context = context,
            goToEnroll = currentGoToEnroll,
            goToAuthentication = currentGoToAuthentication,
            coroutineScope = coroutineScope
        )
    }

}

@Stable
class ScanState(
    private val viewModel: ScanViewModel,
    private val currentGoBack: () -> Unit,
    val lifecycleOwner: LifecycleOwner,
    val context: Context,
    goToEnroll: (EnrollmentChallenge) -> Unit,
    goToAuthentication: (AuthenticationChallenge) -> Unit,
    coroutineScope: CoroutineScope,
) {
    private var torchState: Boolean by mutableStateOf(false)

    var errorData: ErrorData? by mutableStateOf(null)
    var scanComponent: ScanComponent? by mutableStateOf(null)
    var hasCamPermission by mutableStateOf(
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    )

    init {
        viewModel.challenge.observe(lifecycleOwner) { result ->
            when (result) {
                is ChallengeParseResult.Success -> {
                    coroutineScope.launch {
                        delay(200L) // delay a bit, otherwise beep sound is cutoff
                        when (result.value) {
                            is EnrollmentChallenge -> goToEnroll(result.value as EnrollmentChallenge)
                            is AuthenticationChallenge -> goToAuthentication(result.value as AuthenticationChallenge)
                        }
                    }
                }
                is ChallengeParseResult.Failure -> {
                    errorData = ErrorData(result.failure.title, result.failure.message)
                }
            }
        }

    }

    fun toggleTorch() {
        torchState = !torchState
//        scanComponent?.toggleTorch(torchState)
    }

    fun onScanResult(result: String) = viewModel.parseChallenge(result)

    fun dismissErrorDialog() {
        errorData = null
        currentGoBack()
    }

    @SuppressLint("CheckResult")
    fun retryErrorDialog() {
        errorData = null
        scanComponent?.resumeScanning()
    }

    fun camPermissionUpdated(granted: Boolean) {
        hasCamPermission = granted
    }

    fun updateScanComponent(component: ScanComponent) {
        scanComponent = component
    }
}

data class ErrorData(val title: String, val message: String)