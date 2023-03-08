package nl.eduid.screens.requestidstart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.BulletPoint
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.ScaffoldWithTopBarBackButton
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack

@Composable
fun RequestEduIdStartScreen(
    requestId: () -> Unit,
    onBackClicked: () -> Unit,
) = ScaffoldWithTopBarBackButton(onBackClicked = onBackClicked,
    modifier = Modifier
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (content, bottomButton, bottomSpacer) = createRefs()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(content) {
                    top.linkTo(parent.top)
                }
        ) {

            Text(
                text = stringResource(R.string.request_id_screen_title),
                style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Start, color = TextBlack),
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
                .constrainAs(bottomButton) {
                    bottom.linkTo(bottomSpacer.top)
                },
        )
        Spacer(
            Modifier
                .height(40.dp)
                .constrainAs(bottomSpacer) {
                    bottom.linkTo(parent.bottom)
                },
        )
    }
}

@Preview()
@Composable
private fun PreviewEnroll() {
    EduidAppAndroidTheme {
        RequestEduIdStartScreen({},{})
    }
}




