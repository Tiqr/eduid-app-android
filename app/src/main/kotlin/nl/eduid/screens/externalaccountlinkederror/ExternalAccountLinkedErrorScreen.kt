package nl.eduid.screens.externalaccountlinkederror

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun ExternalAccountLinkedErrorScreen(
    goBack: () -> Unit,
) = EduIdTopAppBar(onBackClicked = goBack) { padding ->
    ExternalAccountLinkedErrorScreenContent(
        padding = padding,
        goBack = goBack
    )
}

@Composable
fun ExternalAccountLinkedErrorScreenContent(
    padding: PaddingValues = PaddingValues(),
    goBack: () -> Unit
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .systemBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
        .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(
        text = stringResource(R.string.ExternalAccountLinkingError_Title_COPY),
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSecondary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    )
    Spacer(Modifier.height(88.dp))
    Image(
        painter = painterResource(id = R.drawable.ic_access_denied),
        contentDescription = null,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .width(185.dp)
    )
    Spacer(Modifier.height(44.dp))
    Text(
        text = stringResource(R.string.ExternalAccountLinkingError_Subtitle_COPY),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.weight(1f))
    PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.ExternalAccountLinkingError_TryAnotherOption_COPY),
        onClick = {
            goBack()
        }
    )
    Spacer(Modifier.height(16.dp))

}

@Composable
@Preview
fun ExternalAccountLinkedErrorScreenPreview() {
    EduidAppAndroidTheme {
        ExternalAccountLinkedErrorScreenContent(goBack = {})
    }
}