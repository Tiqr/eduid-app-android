package nl.eduid.screens.dataactivity

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.InfoTab
import nl.eduid.ui.getDateString
import nl.eduid.ui.getDateTimeString
import nl.eduid.ui.theme.ButtonGreen
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun DataAndActivityScreen(
    viewModel: DataAndActivityViewModel,
    onDeleteLoginClicked: () -> Unit,
    goBack: () -> Unit,
) {
    val dataAndActivity by viewModel.dataAndActivity.observeAsState(DataAndActivityData())
    DataAndActivityScreenContent(
        onDeleteLoginClicked = { },
        goBack = goBack,
        dataAndActivity = dataAndActivity,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAndActivityScreenContent(
    onDeleteLoginClicked: () -> Unit,
    goBack: () -> Unit,
    dataAndActivity: DataAndActivityData,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(top = 42.dp, start = 26.dp, end = 26.dp),
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.back_button_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .size(width = 46.dp, height = 46.dp)
                            .clickable {
                                goBack.invoke()
                            },
                        alignment = Alignment.Center
                    )
                },
                title = {
                    Image(
                        painter = painterResource(R.drawable.ic_top_logo),
                        contentDescription = "",
                        modifier = Modifier.size(width = 122.dp, height = 46.dp),
                        alignment = Alignment.Center
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .padding(paddingValues)
                .padding(start = 26.dp, end = 26.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(36.dp))
            Text(
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start,
                    color = ButtonGreen
                ),
                text = stringResource(R.string.data_info_title),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                text = stringResource(R.string.data_info_subtitle),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            if (dataAndActivity.providerList == null) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(80.dp)
                        .width(80.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                )
            } else {
                dataAndActivity.providerList.forEach { provider ->
                    InfoTab(
                        startIconLargeUrl = provider.providerLogoUrl,
                        title = provider.providerName,
                        subtitle = "on ${provider.firstLoginStamp.getDateTimeString()}",
                        onClick = { },
                        endIcon = R.drawable.chevron_down,
                        serviceProviderInfo = provider,
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun PreviewDataAndActivityScreenContent() = EduidAppAndroidTheme {
    DataAndActivityScreenContent(
        onDeleteLoginClicked = { },
        goBack = { },
        dataAndActivity = DataAndActivityData(),
    )
}