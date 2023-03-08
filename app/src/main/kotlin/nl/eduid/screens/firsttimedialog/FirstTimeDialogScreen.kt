package nl.eduid.screens.firsttimedialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import nl.eduid.R
import nl.eduid.screens.start.StartScreen
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.AlertWarningBackground
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstTimeDialogScreen(
) = Scaffold { paddingValues ->
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(horizontal = 40.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.close_x_icon),
            contentDescription = "",
            modifier = Modifier
                .size(width = 48.dp, height = 48.dp)
                .align(Alignment.End),
        )
        Box(Modifier.background(color = AlertWarningBackground)) {
            Column {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = "When you study in the Netherlands and you want to use eduID to logon to an educational services, we need to be sure it’s you and not someone impersonating you.\n" +
                            "\n" +
                            "You must therefore add the following information to your eduID:\n" +
                            "Validation of your full name by a third party \n" +
                            "Proof of being a student\n" +
                            "Your current institution ",
                )
            }
        }
    }
}

@Preview()
@Composable
private fun PreviewFirstTimeDialogScreen() {
    EduidAppAndroidTheme {
        FirstTimeDialogScreen()
    }
}