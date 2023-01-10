package nl.eduid.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nl.eduid.R

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onFirstTimeUser: () -> Unit,
    onSignIn: () -> Unit,
    onResumeApp: () -> Unit,
    modifier: Modifier = Modifier
) = Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    val startupData by viewModel.startupData.observeAsState(Startup.Unknown)
    var animateBackground by remember { mutableStateOf(false) }

    Surface(
        modifier = if (animateBackground) Modifier.fillMaxSize() else Modifier.size(0.dp, 0.dp),
        color = MaterialTheme.colorScheme.primary
    ) {}
    Text(
        text = stringResource(id = R.string.splash_title),
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.fillMaxWidth()
    )

    LaunchedEffect(startupData) {
        if (startupData == Startup.Unknown) {
            animateBackground = true
        } else {
            when (startupData) {
                Startup.RegistrationRequired -> onFirstTimeUser()
                Startup.ShowSignIn -> onSignIn()
                else -> onResumeApp()
            }
        }
    }

}

