package nl.eduid.screens.pinsetup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.PIN_MAX_LENGTH

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationPinSetupScreen(goToPinConfirm: (String) -> Unit, goBack: () -> Unit) = Scaffold(
    topBar = {
        CenterAlignedTopAppBar(modifier = Modifier
            .padding(top = 52.dp, bottom = 40.dp)
            .padding(horizontal = 10.dp),
            navigationIcon = {
                IconButton(onClick = goBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.button_back),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(width = 53.dp, height = 53.dp)
                    )
                }
            },
            title = {
                Image(
                    painter = painterResource(R.drawable.logo_eduid_big),
                    contentDescription = "",
                    modifier = Modifier.size(width = 122.dp, height = 46.dp),
                    alignment = Alignment.Center
                )
            })
    },
    modifier = Modifier.systemBarsPadding(),
) { paddingValues ->

    var pinValue: String by rememberSaveable { mutableStateOf("") }
    var isPinInvalid: Boolean by rememberSaveable { mutableStateOf(false) }

    PinContent(
        pinCode = pinValue,
        pinStep = PinStep.PinCreate,
        isPinInvalid = isPinInvalid,
        title = stringResource(R.string.pinsetup_title),
        description = stringResource(R.string.pinsetup_description),
        label = "",
        onPinChange = { pin, _ ->
            pinValue = pin
        },
        onClick = {
            isPinInvalid = pinValue.length < PIN_MAX_LENGTH
            if (!isPinInvalid) {
                goToPinConfirm(pinValue)
            }
        },
        paddingValues = paddingValues,
        isProcessing = false
    )

}