package nl.eduid.screens.deleteaccountfirstconfirm

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.theme.AlertWarningBackground
import nl.eduid.ui.theme.ButtonRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack
import nl.eduid.ui.theme.TextGreen

@Composable
fun DeleteAccountFirstConfirmScreen(
    goBack: () -> Unit,
    onDeleteAccountPressed: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack,
) {
    DeleteAccountFirstConfirmScreenContent(
        onDeleteAccountPressed = onDeleteAccountPressed,
    )
}

@Composable
private fun DeleteAccountFirstConfirmScreenContent(
    onDeleteAccountPressed: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .weight(1f)
    ) {
        Text(
            text = stringResource(R.string.delete_account_one_title),
            style = MaterialTheme.typography.titleLarge.copy(
                color = TextGreen,
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
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                text = stringResource(R.string.delete_account_one_subtitle),
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
            text = stringResource(R.string.delete_account_description),
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextBlack,
                textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    Button(
        shape = RoundedCornerShape(CornerSize(6.dp)),
        onClick = onDeleteAccountPressed,
        border = BorderStroke(1.dp, Color.Red),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonRed),
        modifier = Modifier
            .sizeIn(minHeight = 56.dp)
            .padding(bottom = 24.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.button_manage_account_delete),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = ButtonRed, fontWeight = FontWeight.SemiBold
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