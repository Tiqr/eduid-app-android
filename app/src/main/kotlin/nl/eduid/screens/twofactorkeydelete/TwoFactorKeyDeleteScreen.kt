package nl.eduid.screens.twofactorkeydelete

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import nl.eduid.R
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.theme.AlertRedBackground
import nl.eduid.ui.theme.ButtonBorderGrey
import nl.eduid.ui.theme.ButtonRed
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.TextBlack
import nl.eduid.ui.theme.TextGreen
import nl.eduid.ui.theme.TextGrey

@Composable
fun TwoFactorKeyDeleteScreen(
    viewModel: TwoFactorKeyDeleteViewModel,
    twoFaKeyId: String,
    goBack: () -> Unit,
) = EduIdTopAppBar(
    onBackClicked = goBack
) {
    var waitForVmEvent by rememberSaveable { mutableStateOf(false) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    viewModel.uiState.errorData?.let { errorData ->
        val context = LocalContext.current
        AlertDialogWithSingleButton(
            title = errorData.title(context),
            explanation = errorData.message(context),
            buttonLabel = stringResource(R.string.button_ok),
            onDismiss = viewModel::dismissError
        )
    }

    if (waitForVmEvent) {
        val currentGoBack by rememberUpdatedState(newValue = goBack)
        LaunchedEffect(viewModel, lifecycle) {
            snapshotFlow { viewModel.uiState }.distinctUntilChanged()
                .filter { it.isCompleted != null }.flowWithLifecycle(lifecycle).collect {
                    waitForVmEvent = false
                    currentGoBack()
                    viewModel.clearCompleted()
                }
        }
    }

    TwoFactorKeyDeleteScreenContent(
        inProgress = viewModel.uiState.inProgress,
        onDeleteClicked = {
            waitForVmEvent = true
            viewModel.deleteKey(twoFaKeyId)
        },
        goBack = goBack,
    )
}

@Composable
private fun TwoFactorKeyDeleteScreenContent(
    inProgress: Boolean = false,
    onDeleteClicked: () -> Unit = {},
    goBack: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
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
                color = TextGreen, textAlign = TextAlign.Start
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
            Image(painter = painterResource(R.drawable.warning_icon_red),
                contentDescription = "",
                modifier = Modifier.constrainAs(image) {
                    top.linkTo(parent.top, margin = 12.dp)
                    start.linkTo(parent.start, margin = 12.dp)
                    end.linkTo(text.start, margin = 12.dp)
                })
            Text(style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                text = stringResource(R.string.delete_two_key_subtitle),
                modifier = Modifier.constrainAs(text) {
                    start.linkTo(image.end)
                    end.linkTo(parent.end, margin = 12.dp)
                    top.linkTo(parent.top, margin = 12.dp)
                    bottom.linkTo(parent.bottom, margin = 12.dp)
                    width = Dimension.fillToConstraints
                })
        }
        Spacer(Modifier.height(18.dp))
        if (inProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(18.dp))
        }
        Text(
            text = stringResource(R.string.delete_two_key_description),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextBlack, textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    Column(
        Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
        ) {
            PrimaryButton(
                enabled = !inProgress,
                text = stringResource(id = R.string.button_cancel),
                modifier = Modifier.widthIn(min = 140.dp),
                onClick = goBack,
                buttonBackgroundColor = Color.Transparent,
                buttonTextColor = TextGrey,
                buttonBorderColor = ButtonBorderGrey,
            )
            PrimaryButton(
                enabled = !inProgress,
                text = stringResource(id = R.string.button_confirm),
                modifier = Modifier.widthIn(min = 140.dp),
                onClick = onDeleteClicked,
                buttonBackgroundColor = ButtonRed,
                buttonTextColor = Color.White,
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Preview()
@Composable
private fun PreviewTwoFactorKeyDeleteScreenContent() {
    EduidAppAndroidTheme {
        TwoFactorKeyDeleteScreenContent(onDeleteClicked = { })
    }
}