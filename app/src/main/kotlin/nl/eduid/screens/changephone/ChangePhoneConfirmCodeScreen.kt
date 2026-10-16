package nl.eduid.screens.changephone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import nl.eduid.R
import nl.eduid.screens.recovery.confirmsms.ConfirmCodeScreen
import nl.eduid.screens.recovery.confirmsms.ConfirmCodeViewModel
import nl.eduid.ui.AlertDialogWithSingleButton

/**
 * Confirms the SMS code for the new recovery phone number, then tells the user the number was updated.
 */
@Composable
fun ChangePhoneConfirmCodeScreen(
    viewModel: ConfirmCodeViewModel,
    phoneNumber: String,
    goBack: () -> Unit,
    onPhoneChanged: () -> Unit,
) {
    var showSuccess by rememberSaveable { mutableStateOf(false) }
    ConfirmCodeScreen(
        viewModel = viewModel,
        phoneNumber = phoneNumber,
        goToStartScreen = { showSuccess = true },
        goBack = goBack,
    )
    if (showSuccess) {
        AlertDialogWithSingleButton(
            title = stringResource(R.string.ChangeSMSRecovery_Success_Title_COPY),
            explanation = stringResource(R.string.ChangeSMSRecovery_Success_Description_COPY),
            buttonLabel = stringResource(R.string.ChangeSMSRecovery_Success_Button_COPY),
            onDismiss = {
                showSuccess = false
                onPhoneChanged()
            },
        )
    }
}
