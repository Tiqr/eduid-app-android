package nl.eduid.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.SplashScreenBackgroundColor

const val SplashWaitTime: Long = 800

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen() = Scaffold(containerColor = SplashScreenBackgroundColor) { paddingValues ->
    SplashContent(
        paddingValues = paddingValues,
    )
}

@Composable
private fun SplashContent(
    paddingValues: PaddingValues,
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
            painter = painterResource(id = R.drawable.ic_correct_logo),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(88.dp)
                .fillMaxWidth(),
        )

        Spacer(Modifier.height(36.dp))

        Text(
            text = stringResource(id = R.string.Splash_Title_COPY),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewSplashScreen() {
    EduidAppAndroidTheme {
        Scaffold(containerColor = SplashScreenBackgroundColor) { paddingValues ->
            SplashContent(
                paddingValues = paddingValues,
            )
        }
    }
}

