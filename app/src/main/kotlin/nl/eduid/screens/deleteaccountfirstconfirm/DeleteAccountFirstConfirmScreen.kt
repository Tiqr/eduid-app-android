package nl.eduid.screens.deleteaccountfirstconfirm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import de.charlex.compose.material3.HtmlText
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.theme.AlertWarningBackground
import nl.eduid.ui.theme.ColorAlertRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.ColorScale_Gray_Black
import nl.eduid.ui.theme.ColorMain_Green_400

@Composable
fun DeleteAccountFirstConfirmScreen(
    goBack: () -> Unit,
    onDeleteAccountPressed: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack,
) {
    DeleteAccountFirstConfirmScreenContent(
        padding = it,
        onDeleteAccountPressed = onDeleteAccountPressed,
    )
}

@Composable
private fun DeleteAccountFirstConfirmScreenContent(
    padding: PaddingValues = PaddingValues(),
    onDeleteAccountPressed: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .navigationBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.DeleteAccount_Title_COPY),
            style = MaterialTheme.typography.titleLarge.copy(
                color = ColorMain_Green_400,
                textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(18.dp))
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = AlertWarningBackground)
        ) {
            val (image, text) = createRefs()
            Image(
                painter = painterResource(R.drawable.warning_icon_yellow),
                contentDescription = "",
                modifier = Modifier
                    .constrainAs(image) {
                        top.linkTo(parent.top, margin = 12.dp)
                        start.linkTo(parent.start, margin = 12.dp)
                        end.linkTo(text.start, margin = 12.dp)
                    }
            )
            HtmlText(
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                text = stringResource(R.string.DeleteAccount_Disclaimer_COPY),
                modifier = Modifier
                    .constrainAs(text) {
                        start.linkTo(image.end)
                        end.linkTo(parent.end, margin = 12.dp)
                        top.linkTo(parent.top, margin = 12.dp)
                        bottom.linkTo(parent.bottom, margin = 12.dp)
                        width = Dimension.fillToConstraints
                    }
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.DeleteAccount_LongDescription_COPY),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = ColorScale_Gray_Black,
                textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    Button(
        shape = RoundedCornerShape(CornerSize(6.dp)),
        onClick = onDeleteAccountPressed,
        border = BorderStroke(1.dp, Color.Red),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorAlertRed),
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 56.dp),
    ) {
        Text(
            text = stringResource(R.string.DeleteAccount_DeleteAccountButton_COPY),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = ColorAlertRed, fontWeight = FontWeight.SemiBold
            )
        )
    }
}


@Preview()
@Composable
private fun PreviewDeleteAccountFirstConfirmScreenScreen() {
    EduidAppAndroidTheme {
        DeleteAccountFirstConfirmScreenContent()
    }
}