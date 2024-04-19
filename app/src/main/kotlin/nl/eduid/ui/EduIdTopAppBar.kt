package nl.eduid.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.MainSurfGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduIdTopAppBar(
    onBackClicked: () -> Unit = {},
    withBackIcon: Boolean = true,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets
        .exclude(WindowInsets.navigationBars)
        .exclude(WindowInsets.ime),
    content: @Composable (PaddingValues) -> Unit,
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topBarState)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(modifier = Modifier
                    .padding(12.dp), action = {
                    TextButton(
                        onClick = data::performAction,
                    ) { Text(data.visuals.actionLabel ?: "") }
                }) {
                    Text(data.visuals.message)
                }
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                navigationIcon = {
                    if (withBackIcon) {
                        IconButton(
                            onClick = onBackClicked, modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.PinAndBioMetrics_Button_Back_COPY),
                                tint = MainSurfGreen,
                                modifier = Modifier.size(width = 48.dp, height = 48.dp)
                            )
                        }
                    }
                },
                title = {
                    Image(
                        painter = painterResource(R.drawable.ic_correct_logo),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(36.dp),
                        alignment = Alignment.Center
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = contentWindowInsets,
    ) { paddingValues ->
        content(paddingValues)
    }
}

@Preview
@Composable
private fun Preview_TopAppBarWithBackButton() {
    EduidAppAndroidTheme {
        EduIdTopAppBar(onBackClicked = { }) {}
    }
}