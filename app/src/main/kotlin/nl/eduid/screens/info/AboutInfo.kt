package nl.eduid.screens.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.BuildConfig
import nl.eduid.R
import nl.eduid.ui.PrimaryButton

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
fun AboutInfo(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
) {
    FlowColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .systemBarsPadding()
            .padding(horizontal = 8.dp, vertical = 24.dp)
    ) {
        val uriHandler = LocalUriHandler.current

        Text(
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            text = stringResource(id = R.string.app_name_with_version, BuildConfig.VERSION_NAME),
            modifier = Modifier
                .fillMaxWidth()
        )
        Image(
            painter = painterResource(R.drawable.ic_correct_logo),
            contentDescription = "",
            modifier = Modifier
                .size(width = 122.dp, height = 46.dp)
                .align(Alignment.CenterHorizontally),
            alignment = Alignment.Center
        )
        Text(
            style = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
            text = stringResource(id = R.string.Security_ProvidedBy_COPY),
            modifier = Modifier
                .fillMaxWidth()
        )
        Image(
            painter = painterResource(R.drawable.ic_surf_logo),
            contentDescription = "",
            modifier = Modifier
                .size(width = 122.dp, height = 46.dp)
                .align(Alignment.CenterHorizontally)
                .clickable {
                    uriHandler.openUri("https://surf.nl")
                },
            alignment = Alignment.Center
        )
        PrimaryButton(
            text = stringResource(R.string.Generic_RequestError_CloseButton_COPY),
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        )

    }
}