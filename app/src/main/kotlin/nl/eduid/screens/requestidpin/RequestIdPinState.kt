package nl.eduid.screens.requestidpin

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import nl.eduid.screens.biometric.Biometricks

@Composable
fun rememberRequestIdState(
    viewModel: RequestIdPinViewModel,
    onPinVerified: () -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): RequestIdState {
    val currentOnPinVerifiedDone by rememberUpdatedState(onPinVerified)
    return remember(viewModel, lifecycleOwner, context, coroutineScope) {
        RequestIdState(
            viewModel = viewModel,
            onPinVerified = currentOnPinVerifiedDone,
            lifecycleOwner = lifecycleOwner,
            coroutineScope = coroutineScope
        )
    }
}

@Stable
class RequestIdState(
    private val viewModel: RequestIdPinViewModel,
    val coroutineScope: CoroutineScope,
    onPinVerified: () -> Unit,
    lifecycleOwner: LifecycleOwner,
) {
    var pinCode: String by mutableStateOf("")
    var biometrics: Biometricks by mutableStateOf(Biometricks.NoneEnrolled)
    private var validationJob: Job? = null


    init {
        viewModel.pinValid.observe(lifecycleOwner) { authenticateState ->
            validationJob?.cancel()
            when (authenticateState) {
                true -> {
                    validationJob = coroutineScope.launch {
                        //show success
                    }
                    onPinVerified()
                }
                false -> {
                    validationJob = coroutineScope.launch {
                        //show error
                    }
                }
                else -> {}
            }
        }
    }

    fun confirmPin() = viewModel.isPinValid(
        pinCode
    )

    fun onPinChange(newPin: String) {
        pinCode = newPin
    }
}