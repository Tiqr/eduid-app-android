package nl.eduid.screens.requestidpin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.PinInputField
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.ScaffoldWithTopBarBackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestIdPinScreen(
    onPinVerified: () -> Unit,
    viewModel: RequestIdPinViewModel,
    state: RequestIdState = rememberRequestIdState(
        viewModel = viewModel, onPinVerified = onPinVerified
    ),
    goBack: () -> Unit,
) = ScaffoldWithTopBarBackButton(
    onBackClicked = goBack,
    modifier = Modifier
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(horizontal = 8.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.request_id_pin_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.request_id_pin_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))

            PinInputField(
                label = null,
                pinCode = state.pinCode,
                onPinChange = { state.onPinChange(it) },
                submitPin = state::confirmPin,
                isPinInvalid = false,
                pinMaxLength = 6
            )
            Spacer(modifier = Modifier.height(52.dp))
        }
        PrimaryButton(
            text = stringResource(R.string.request_id_pin_button),
            onClick = state::confirmPin,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(40.dp))
    }
}
