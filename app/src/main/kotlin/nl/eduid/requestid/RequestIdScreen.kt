package nl.eduid.requestid

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.ui.BulletPoint
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestIdStartScreen(
    requestId: () -> Unit,
    onBackClicked: () -> Unit,
) = Scaffold { paddingValues ->

    ConstraintLayout(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(horizontal = 40.dp)
            .systemBarsPadding()
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

            Spacer(
                modifier = Modifier.height(40.dp)
            )

            ConstraintLayout(Modifier
                .fillMaxWidth()
            ) {
                val (backButton, logo) = createRefs()
                Image(
                    painter = painterResource(id = R.drawable.logo_eduid_big),
                    contentDescription = "",
                    modifier = Modifier
                        .size(width = 122.dp, height = 46.dp)
                        .constrainAs(logo){
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.back_button_icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(width = 53.dp, height = 53.dp)
                        .clickable { onBackClicked() }
                        .constrainAs(backButton){
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                )
            }

            Spacer(
                modifier = Modifier.height(40.dp)
            )

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
        RequestIdStartScreen({},{})
    }
}




