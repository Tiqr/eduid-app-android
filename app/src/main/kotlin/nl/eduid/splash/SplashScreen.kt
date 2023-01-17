package nl.eduid.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.SplashScreenBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onFirstTimeUser: () -> Unit,
    onLogin: () -> Unit,
    onResumeApp: () -> Unit,
) = Scaffold { paddingValues ->
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(SplashScreenBackgroundColor)
            .padding(paddingValues)
    ) {

        val startupData by viewModel.startupData.observeAsState(Startup.Unknown)
        var animateBackground by remember { mutableStateOf(false) }

        Surface(
            modifier = if (animateBackground) Modifier.fillMaxSize() else Modifier.size(0.dp, 0.dp),
            color = SplashScreenBackgroundColor
        ) {
            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_eduid_big),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                    )

                    Spacer(Modifier.height(36.dp))

                    Text(
                        text = stringResource(id = R.string.splash_title),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 64.dp),
                    )

                    Spacer(Modifier.height(72.dp))
                }
                Image(
                    painter = painterResource(id = R.drawable.splash_footer_logo),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .sizeIn(minWidth = 140.dp, minHeight = 32.dp)
                        .padding(horizontal = 64.dp, vertical = 24.dp),
                )
            }
        }

        LaunchedEffect(startupData) {
            if (startupData == Startup.Unknown) {
                animateBackground = true
            } else {
                when (startupData) {
                    Startup.RegistrationRequired -> onFirstTimeUser()
                    Startup.ShowSignIn -> onLogin()
                    else -> onResumeApp()
                }
            }
        }
    }

}

@Preview
@Composable
private fun PreviewSplashScreen() {
    EduidAppAndroidTheme {

    }
}

