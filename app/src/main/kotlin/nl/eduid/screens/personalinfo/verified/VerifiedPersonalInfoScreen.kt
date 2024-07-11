package nl.eduid.screens.personalinfo.verified

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.eduid.R
import nl.eduid.screens.personalinfo.PersonalInfo
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.VerifiedInfoField
import nl.eduid.ui.getDateString
import nl.eduid.ui.theme.ColorScale_Gray_200
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun VerifiedPersonalInfoRoute(
    viewModel: VerifiedPersonalInfoViewModel,
    goBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorData by viewModel.errorData.collectAsStateWithLifecycle()
    var waitingForVmEvent by rememberSaveable { mutableStateOf(false) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val onBack = remember(viewModel) { { goBack() } }
    val remove = remember(viewModel) {
        {
            waitingForVmEvent = true
            viewModel.removeConnection()
        }
    }
    errorData?.let {
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = it.title(context),
            explanation = it.message(context),
            buttonLabel = stringResource(R.string.Button_OK_COPY),
            onDismiss = viewModel::clearErrorData
        )
    }
    if (waitingForVmEvent) {
        val currentGoBack by rememberUpdatedState(goBack)
        LaunchedEffect(key1 = viewModel, key2 = lifecycle) {
            currentGoBack()
            waitingForVmEvent = false
        }
    }

    VerifiedPersonalInfoScreen(
        verifier = uiState.verifier,
        isLoading = uiState.isLoading,
        onRemoveConnection = remove,
        goBack = onBack
    )
}

@Composable
fun VerifiedPersonalInfoScreen(
    verifier: PersonalInfo.InstitutionAccount?,
    isLoading: Boolean,
    onRemoveConnection: () -> Unit,
    goBack: () -> Unit,
    modifier: Modifier = Modifier,
) = EduIdTopAppBar(
    onBackClicked = { if (!isLoading) goBack() },
) { padding ->
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSecondary
            ),
            text = stringResource(R.string.YourVerifiedInformation_Title_COPY),
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(top = 16.dp)
        )
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(R.string.YourVerifiedInformation_Subtitle_COPY),
        )
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.homepage_info_icon),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(R.string.YourVerifiedInformation_ExplainIcon_COPY),
            )
        }
        HorizontalDivider(
            color = ColorScale_Gray_200, modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSecondary
            ), text = stringResource(
                R.string.YourVerifiedInformation_FromInstitution_COPY,
                verifier?.institution.orEmpty()
            ), modifier = Modifier.fillMaxWidth()
        )
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                append(stringResource(R.string.YourVerifiedInformation_ReceivedOn_COPY))
                pushStyle(
                    MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ).toSpanStyle()
                )
                append(verifier?.createdStamp?.getDateString() ?: "")
            },
        )
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                append(stringResource(R.string.YourVerifiedInformation_ValidUntil_COPY))
                pushStyle(
                    MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ).toSpanStyle()
                )
                append(verifier?.expiryStamp?.getDateString() ?: "")
            },
        )
        verifier?.givenName?.let {
            VerifiedInfoField(
                title = it, subtitle = stringResource(R.string.Profile_VerifiedGivenName_COPY)
            )
        }
        verifier?.familyName?.let {
            VerifiedInfoField(
                title = it, subtitle = stringResource(R.string.Profile_VerifiedFamilyName_COPY)
            )
        }
        VerifiedInfoField(
            title = verifier?.role.orEmpty(), subtitle = stringResource(
                id = R.string.YourVerifiedInformation_AtInstitution_COPY,
                verifier?.institution.orEmpty()
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onRemoveConnection()
                }
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "",
                modifier = Modifier.size(24.dp)
            )
            Text(
                style = MaterialTheme.typography.bodyLarge, text = buildAnnotatedString {
                    pushStyle(
                        MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.colorScheme.primary
                        ).toSpanStyle()
                    )
                    append(stringResource(R.string.YourVerifiedInformation_RemoveThisInformation_COPY))
                    pop()
                    append(stringResource(id = R.string.YourVerifiedInformation_FromYourEduID_COPY))
                }, modifier = Modifier
            )
        }
    }
}

@Preview(locale = "en", showBackground = true)
@Preview(locale = "nl", showBackground = true)
@Composable
private fun Preview_VerifiedPersonalInfoScreen() = EduidAppAndroidTheme {
    VerifiedPersonalInfoScreen(
        verifier = PersonalInfo.generateInstitutionAccountList().first(),
        isLoading = false,
        onRemoveConnection = {},
        goBack = {},
    )
}