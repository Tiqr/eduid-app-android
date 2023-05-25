package nl.eduid.screens.requestidstart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.BulletPoint
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack

@Composable
fun RequestEduIdStartScreen(
    requestId: () -> Unit,
    onBackClicked: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = onBackClicked,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.request_id_screen_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start,
                    color = TextBlack
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Text(
                text = stringResource(R.string.request_id_screen_header_text),
                style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            BulletPoint(
                text = stringResource(R.string.request_id_screen_bullet_point_1),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                modifier = Modifier
                    .fillMaxWidth()
            )

            BulletPoint(
                text = stringResource(R.string.request_id_screen_bullet_point_2),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                modifier = Modifier
                    .fillMaxWidth()
            )

            BulletPoint(
                text = stringResource(R.string.request_id_screen_bullet_point_3),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        PrimaryButton(
            text = stringResource(R.string.request_id_screen_create_id_button),
            onClick = requestId,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
        )
    }
}

@Preview()
@Composable
private fun PreviewEnroll() {
    EduidAppAndroidTheme {
        RequestEduIdStartScreen({}, {})
    }
}




