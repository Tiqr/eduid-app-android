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
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun RegistrationExplanation(modifier: Modifier = Modifier) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Bottom,
    modifier = modifier
) {
    Text(
        text = stringResource(R.string.scan_registration_explaination),
        style = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.fillMaxWidth()
    )
    val annotatedString = with(AnnotatedString.Builder()) {
        pushStyle(
            MaterialTheme.typography.bodyLarge.copy(color = Color.White).toSpanStyle()
        )
        append(stringResource(R.string.scan_registration_explaination1a))
        append(" ")
        pop()
        pushStyle(
            MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                textDecoration = TextDecoration.Underline
            ).toSpanStyle()
        )
        append(stringResource(R.string.scan_registration_explaination1b))
        pop()
        append(" ")
        pushStyle(
            MaterialTheme.typography.bodyLarge.copy(color = Color.White).toSpanStyle()
        )
        append(stringResource(R.string.scan_registration_explaination1c))
        toAnnotatedString()
    }

    Text(
        text = annotatedString, style = MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Center,
        ), modifier = Modifier.fillMaxWidth()
    )
    Text(
        text = stringResource(R.string.scan_registration_explaination2),
        style = MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Center,
            color = Color.White,
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        text = stringResource(R.string.scan_registration_explaination3),
        style = MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Center,
            color = Color.White,
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