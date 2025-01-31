package nl.eduid.screens.verifywithid.intro

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.AlertWarningBackground
import nl.eduid.ui.theme.EduidAppAndroidTheme


@Composable
fun VerifyWithIdIntroScreen(
    goBack: () -> Unit,
    goToEnterDetails: () -> Unit
) = EduIdTopAppBar(
    onBackClicked = goBack
) { padding ->
    VerifyWithIdIntroScreenContent(
        goToEnterDetails = goToEnterDetails,
        padding = padding
    )
}

@Composable
fun VerifyWithIdIntroScreenContent(
    goToEnterDetails: () -> Unit,
    padding: PaddingValues = PaddingValues(),
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .systemBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdIntro_Title_FirstLine_COPY),
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdIntro_Title_SecondLine_COPY),
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)

        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdIntro_Description_ServiceDesk_COPY),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.ConfirmIdentityWithIdIntro_Description_Steps_Header_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        val steps = listOf(
            R.string.ConfirmIdentityWithIdIntro_Description_Steps_Step1_COPY,
            R.string.ConfirmIdentityWithIdIntro_Description_Steps_Step2_COPY,
            R.string.ConfirmIdentityWithIdIntro_Description_Steps_Step3_COPY
        )
        steps.forEachIndexed { index, step ->
            Row {
                Text(
                    text = "${index + 1}.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(step),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        Spacer(Modifier.height(48.dp))
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = AlertWarningBackground)
        ) {
            val (image, topText, bottomText) = createRefs()
            Image(
                painter = painterResource(R.drawable.warning_icon_yellow),
                contentDescription = "",
                modifier = Modifier
                    .constrainAs(image) {
                        top.linkTo(parent.top, margin = 12.dp)
                        start.linkTo(parent.start, margin = 12.dp)
                    }
            )
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(R.string.ConfirmIdentityWithIdIntro_ValidDocumentsDisclaimer_List_COPY),
                modifier = Modifier
                    .constrainAs(topText) {
                        start.linkTo(image.end, margin = 12.dp)
                        end.linkTo(parent.end, margin = 12.dp)
                        top.linkTo(parent.top, margin = 12.dp)
                        width = Dimension.fillToConstraints
                    }
            )
            Text(
                style = MaterialTheme.typography.bodySmall,
                text = stringResource(R.string.ConfirmIdentityWithIdIntro_ValidDocumentsDisclaimer_Asterisk_COPY),
                modifier = Modifier
                    .constrainAs(bottomText) {
                        start.linkTo(image.end, margin = 12.dp)
                        end.linkTo(parent.end, margin = 12.dp)
                        top.linkTo(topText.bottom)
                        bottom.linkTo(parent.bottom, margin = 18.dp)
                        width = Dimension.fillToConstraints
                    }
            )
        }
        Spacer(Modifier.height(72.dp))
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.ConfirmIdentityWithIdIntro_EnterDetailsButton_COPY),
            onClick = goToEnterDetails
        )
    }
}

@Composable
@Preview
fun VerifyWithIdIntroScreenContent_Preview() {
    EduidAppAndroidTheme {
        VerifyWithIdIntroScreenContent(
            goToEnterDetails = {}
        )
    }
}