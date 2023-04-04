package nl.eduid.screens.manageaccount

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
import nl.eduid.R
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.AlertWarningBackground
import nl.eduid.ui.theme.ButtonRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack
import nl.eduid.ui.theme.TextGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAccountScreen(
    viewModel: ManageAccountViewModel,
    goBack: () -> Unit,
    onDeleteAccountPressed: () -> Unit,
    dateString: String,
) = Scaffold(
    topBar = {
        CenterAlignedTopAppBar(
            modifier = Modifier.padding(top = 42.dp, start = 26.dp, end = 26.dp),
            navigationIcon = {
                Image(
                    painter = painterResource(R.drawable.back_button_icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(width = 46.dp, height = 46.dp)
                        .clickable {
                            goBack.invoke()
                        },
                    alignment = Alignment.Center
                )
            },
            title = {
                Image(
                    painter = painterResource(R.drawable.ic_top_logo),
                    contentDescription = "",
                    modifier = Modifier.size(width = 122.dp, height = 46.dp),
                    alignment = Alignment.Center
                )
            },
        )
    },
) { paddingValues ->
    ManageAccountScreenContent(
        paddingValues = paddingValues,
        onDeleteAccountPressed = onDeleteAccountPressed,
        dateString = dateString,
    )
}

@Composable
private fun ManageAccountScreenContent(
    paddingValues: PaddingValues = PaddingValues(),
    onDeleteAccountPressed: () -> Unit,
    dateString: String,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.manage_account_title),
                style = MaterialTheme.typography.titleLarge.copy(color = TextGreen, textAlign = TextAlign.Start),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "${stringResource(R.string.manage_account_subtitle)} $dateString",
                style = MaterialTheme.typography.bodyLarge.copy(color = TextBlack, textAlign = TextAlign.Start),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            Box(Modifier
                .background(color = AlertWarningBackground)
                .padding(12.dp)
            ) {
                Column {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = stringResource(R.string.manage_account_info_block),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                text = "Download my data",
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp)
        ) {
            Button(
                shape = RoundedCornerShape(CornerSize(6.dp)),
                onClick = onDeleteAccountPressed,
                border = BorderStroke(1.dp, Color.Red),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonRed),
                modifier = Modifier
                    .sizeIn(minHeight = 48.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Delete your account",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = ButtonRed, fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}


@Preview()
@Composable
private fun PreviewManageAccountScreen() {
    EduidAppAndroidTheme {
        ManageAccountScreenContent(PaddingValues(),{},"15 March 2004")
    }
}