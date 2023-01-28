package nl.eduid.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.theme.EduidAppAndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithTopBarBackButton(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) =
    Scaffold(modifier = modifier.systemBarsPadding(), topBar = {
        CenterAlignedTopAppBar(modifier = Modifier
            .padding(top = 52.dp, bottom = 40.dp)
            .padding(horizontal = 10.dp), navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.button_back),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(width = 53.dp, height = 53.dp)
                )
            }
        }, title = {
            Image(
                painter = painterResource(R.drawable.logo_eduid_big),
                contentDescription = "",
                modifier = Modifier.size(width = 122.dp, height = 46.dp),
                alignment = Alignment.Center
            )
        })
    }) { paddingValues ->
        Row(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 30.dp)
                .fillMaxSize()
        ) {
            content()
        }
    }


@Preview
@Composable
private fun Preview_TopAppBarWithBackButton() {
    EduidAppAndroidTheme {
        ScaffoldWithTopBarBackButton(
            { }, content = {}
        )
    }
}