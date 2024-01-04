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
import nl.eduid.ErrorData
import org.tiqr.data.model.Challenge
import org.tiqr.data.model.ChallengeParseResult
import org.tiqr.data.scan.ScanComponent
import java.util.*


@Composable
fun rememberScanState(
    viewModel: StatelessScanViewModel,
    goBack: () -> Unit,
    goToNext: (Challenge) -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): ScanState {
    val currentGoBack by rememberUpdatedState(newValue = goBack)
    val currentGoToNext by rememberUpdatedState(newValue = goToNext)

    return remember(viewModel, lifecycleOwner, context, coroutineScope) {
        ScanState(
            viewModel = viewModel,
            currentGoBack = currentGoBack,
            coroutineScope = coroutineScope,
            goToNext = currentGoToNext,
            context = context,
            lifecycleOwner = lifecycleOwner
        )
    }

}

@Stable
class ScanState(
    private val viewModel: StatelessScanViewModel,
    private val currentGoBack: () -> Unit,
    private val coroutineScope: CoroutineScope,
    private val goToNext: (Challenge) -> Unit,
    val context: Context,
    val lifecycleOwner: LifecycleOwner,
) {
    private var torchState: Boolean by mutableStateOf(false)

    var errorData: ErrorData? by mutableStateOf(null)
    var scanComponent: ScanComponent? by mutableStateOf(null)
    var hasCamPermission by mutableStateOf(
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    )

    fun toggleTorch() {
        torchState = !torchState
        scanComponent?.toggleTorch(torchState)
    }

    fun onScanResult(result: String) = coroutineScope.launch {
        val parseResult = viewModel.parseChallenge(result)
        when (parseResult) {
            is ChallengeParseResult.Success -> {
                delay(200L) // delay a bit, otherwise beep sound is cutoff
                goToNext(parseResult.value)
            }

            is ChallengeParseResult.Failure -> {
                errorData = ErrorData(parseResult.failure.title, parseResult.failure.message)
            }
        }
    }

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