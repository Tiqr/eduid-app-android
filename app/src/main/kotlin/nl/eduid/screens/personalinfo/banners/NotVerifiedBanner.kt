package nl.eduid.screens.personalinfo.banners

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.theme.ColorSupport_Blue_100

@Composable
fun NotVerifiedBanner(addLinkToAccount: () -> Unit = {}) {
    val configuration = LocalConfiguration.current
    Column(
        modifier = Modifier
            .requiredWidth(configuration.screenWidthDp.dp)
            .background(ColorSupport_Blue_100)
            .padding(vertical = 12.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Spacer(modifier = Modifier.size(4.dp))
                Image(
                    painter = painterResource(R.drawable.shield_tick_blue),
                    contentDescription = ""
                )
            }
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                text = stringResource(R.string.Profile_VerifyNow_Title_COPY),
            )
        }
        Row {
            Spacer(modifier = Modifier.width(33.dp))
            OutlinedButton(
                onClick = addLinkToAccount,
                shape = RoundedCornerShape(CornerSize(6.dp)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                modifier = Modifier
                    .sizeIn(minHeight = 40.dp),
            ) {
                Text(
                    text = stringResource(R.string.Profile_VerifyNow_Button_COPY),
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                )
            }
        }
    }
}
