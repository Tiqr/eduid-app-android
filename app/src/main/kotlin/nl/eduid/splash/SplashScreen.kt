package nl.eduid.splash

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
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
    onAppReady: () -> Unit,
) = Scaffold(containerColor = SplashScreenBackgroundColor) { paddingValues ->
    val startupData: Startup by viewModel.startupData.observeAsState(Startup.Unknown)
    SplashContent(
        startupData = startupData,
        paddingValues = paddingValues,
        onFirstTimeUser = onFirstTimeUser,
        onAppReady = onAppReady
    )
}

@Composable
private fun SplashContent(
    startupData: Startup,
    paddingValues: PaddingValues,
    onFirstTimeUser: () -> Unit,
    onAppReady: () -> Unit,
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
) {
    val uriHandler = LocalUriHandler.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
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
                color = Color.White, textAlign = TextAlign.Center
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
            .clickable { uriHandler.openUri("https://surf.nl") }
            .sizeIn(minWidth = 140.dp, minHeight = 32.dp)
            .padding(horizontal = 64.dp, vertical = 24.dp),
    )

    LaunchedEffect(startupData) {
        when (startupData) {
            Startup.Unknown -> {}
            Startup.RegistrationRequired -> onFirstTimeUser()
            Startup.AppReady -> onAppReady()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewSplashScreen() {
    EduidAppAndroidTheme {
        Scaffold(containerColor = SplashScreenBackgroundColor) { paddingValues ->
            SplashContent(
                startupData = Startup.RegistrationRequired,
                paddingValues = paddingValues,
                onFirstTimeUser = {},
                onAppReady = {},
            )
        }
    }
}

