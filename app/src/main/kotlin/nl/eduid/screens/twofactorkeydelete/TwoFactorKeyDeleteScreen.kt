package nl.eduid.screens.twofactorkeydelete

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.AlertRedBackground
import nl.eduid.ui.theme.ButtonBorderGrey
import nl.eduid.ui.theme.ButtonRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack
import nl.eduid.ui.theme.TextGreen
import nl.eduid.ui.theme.TextGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoFactorKeyDeleteScreen(
    viewModel: TwoFactorKeyDeleteViewModel,
    twoFaKeyId: String,
    onDeleteConfirmed: () -> Unit,
    goBack: () -> Unit,
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
    TwoFactorKeyDeleteScreenContent(
        paddingValues = paddingValues,
        onDeleteClicked = { viewModel.deleteKey(twoFaKeyId, onDeleteConfirmed) },
        goBack = goBack,
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TwoFactorKeyDeleteScreenContent(
    paddingValues: PaddingValues = PaddingValues(),
    onDeleteClicked: () -> Unit = {},
    goBack: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp)
    ) {
        Spacer(Modifier.height(36.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.delete_two_key_title),
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
                    .background(color = AlertRedBackground)
            ) {
                val (image, text) = createRefs()
                Image(
                    painter = painterResource(R.drawable.warning_icon_red),
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
                    text = stringResource(R.string.delete_two_key_subtitle),
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
                text = stringResource(R.string.delete_two_key_description),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextBlack,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                PrimaryButton(
                    modifier = Modifier.widthIn(min = 140.dp),
                    text = "Cancel",
                    onClick = goBack,
                    buttonBackgroundColor = Color.Transparent,
                    buttonTextColor = TextGrey,
                    buttonBorderColor = ButtonBorderGrey,
                )
                PrimaryButton(
                    modifier = Modifier.widthIn(min = 140.dp),
                    text = "Confirm",
                    onClick = onDeleteClicked,
                    buttonBackgroundColor = ButtonRed,
                    buttonTextColor = Color.White,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview()
@Composable
private fun PreviewTwoFactorKeyDeleteScreenContent() {
    EduidAppAndroidTheme {
        TwoFactorKeyDeleteScreenContent(onDeleteClicked = { })
    }
}