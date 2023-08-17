package nl.eduid.screens.dataactivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LinearProgressIndicator
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
import nl.eduid.R
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.theme.AlertRedBackground
import nl.eduid.ui.theme.ButtonRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextGreen

@Composable
fun DeleteServiceContent(
    providerName: String,
    inProgress: Boolean = false,
    paddingValues: PaddingValues = PaddingValues(),
    removeService: () -> Unit = {},
    goBack: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.delete_service_confirm_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextGreen, textAlign = TextAlign.Start
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(18.dp))
            if (inProgress) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = AlertRedBackground)
            ) {
                Image(
                    painter = painterResource(R.drawable.warning_icon_red),
                    contentDescription = "",
                    modifier = Modifier.padding(12.dp)
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    text = stringResource(R.string.delete_no_undo_warning),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = stringResource(
                    R.string.delete_service_confirm_explanation, providerName
                ),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(36.dp))
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
        ) {
            SecondaryButton(
                text = stringResource(R.string.button_cancel),
                onClick = goBack,
                enabled = !inProgress,
                modifier = Modifier.widthIn(min = 140.dp),
            )
            PrimaryButton(
                text = stringResource(R.string.button_confirm),
                onClick = removeService,
                enabled = !inProgress,
                modifier = Modifier.widthIn(min = 140.dp),
                buttonBackgroundColor = ButtonRed,
                buttonTextColor = Color.White,
            )
        }
    }
}

@Preview()
@Composable
private fun PreviewDeleteServiceScreen() {
    EduidAppAndroidTheme {
        DeleteServiceContent(
            providerName = "OpenConext Profile",
        )
    }
}