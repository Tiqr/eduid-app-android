package nl.eduid.screens.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import nl.eduid.R
import nl.eduid.ui.annotatedStringWithBoldParts
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun RegistrationExplanation(modifier: Modifier = Modifier) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Bottom,
    modifier = modifier
) {
    Text(
        text = annotatedStringWithBoldParts(
            stringResource(R.string.ScanView_MainText_COPY),
            stringResource(R.string.ScanView_MainTextBoldPart_COPY),
            boldWeight = FontWeight.Bold
            ),
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.SemiBold
        ),
        modifier = Modifier.fillMaxWidth()
    )
}


@Preview
@Composable
private fun Preview_RegistrationExplanation() {
    EduidAppAndroidTheme {
        RegistrationExplanation()
    }
}