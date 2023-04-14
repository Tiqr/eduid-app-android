package nl.eduid.screens.editname

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.screens.firsttimedialog.LinkAccountContract
import nl.eduid.screens.personalinfo.PersonalInfoViewModel
import nl.eduid.screens.personalinfo.UiState
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.LinkAccountCard

@Composable
fun EditNameScreen(
    viewModel: PersonalInfoViewModel,
    goToAccountLinked: () -> Unit = {},
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack,
) {
    val uiState by viewModel.uiState.observeAsState(UiState())
    var isGettingLinkUrl by rememberSaveable { mutableStateOf(false) }
    var isLinkingStarted by rememberSaveable { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(contract = LinkAccountContract(), onResult = { _ ->
            if (isLinkingStarted) {
                isLinkingStarted = false
                goToAccountLinked()
            }
        })

    if (isGettingLinkUrl && uiState.haveValidLinkIntent()) {
        LaunchedEffect(key1 = viewModel) {
            isGettingLinkUrl = false
            launcher.launch(uiState.linkUrl)
            isLinkingStarted = true
        }
    }

    EditNameContent(
        givenName = uiState.personalInfo.seflAssertedName.givenName.orEmpty(),
        familyName = uiState.personalInfo.seflAssertedName.familyName.orEmpty(),
        updateName = {givenName, familyName-> viewModel.updateName(givenName, familyName)}
    )
}

@Composable
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
private fun EditNameContent(
    givenName: String,
    familyName: String,
    updateName: (String, String) -> Unit = { _, _ -> },
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        var givenName by rememberSaveable { mutableStateOf(givenName) }
        var familyName by rememberSaveable { mutableStateOf(familyName) }
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = stringResource(R.string.edit_name_title),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            style = MaterialTheme.typography.titleLarge.copy(
                color = ButtonGreen
            ),
            text = stringResource(R.string.edit_name_subtitle),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = givenName,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }),
            onValueChange = { givenName = it },
            label = { Text(stringResource(R.string.request_id_details_screen_first_name_input_title)) },
            placeholder = { Text(stringResource(R.string.request_id_details_screen_first_name_input_hint)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = familyName,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            onValueChange = { familyName = it },
            label = { Text(stringResource(R.string.request_id_details_screen_last_name_input_title)) },
            placeholder = { Text(stringResource(R.string.request_id_details_screen_last_name_input_hint)) },
            modifier = Modifier.fillMaxWidth()
        )
        PrimaryButton(
            text = stringResource(id = R.string.button_update),
            enabled = (familyName.isEmpty() || givenName.isEmpty()),
            onClick = { updateName(givenName, familyName) },
            modifier = Modifier.fillMaxWidth()
        )

        LinkAccountCard(title = R.string.personalinfo_add_role_institution,
            subtitle = R.string.personalinfo_add_via,
            addLinkToAccount = {})

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}


@Preview
@Composable
private fun Preview_LinkAccountCard() {
    EduidAppAndroidTheme {
        EditNameContent(
            givenName = "Sam", familyName = "Vines",
        )
    }
}