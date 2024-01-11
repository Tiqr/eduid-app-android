package nl.eduid.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.flags.FeatureFlag
import nl.eduid.flags.GroupedTestSetting
import nl.eduid.ui.AlertDialogWithSingleButton
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.theme.EduidAppAndroidTheme
import org.tiqr.data.util.extension.openAppSystemSettings

@Composable
fun EditFeatureFlagsScreen(
    viewModel: EditFeatureFlagsViewModel,
) = EduIdTopAppBar(
    withBackIcon = false
) {
    val context = LocalContext.current
    viewModel.uiState.showForceStopPrompt?.let {
        AlertDialogWithSingleButton(
            title = stringResource(R.string.editflags_app_must_restart),
            explanation = stringResource(R.string.editflags_app_restart_explained),
            buttonLabel = stringResource(R.string.editflags_open_settings),
            onDismiss = {
                context.openAppSystemSettings()
                viewModel.clearForceStopPrompt()
            },
        )
    }
    EditFeatureFlagContent(
        testSettings = viewModel.uiState.featureFlagsData,
        isForTestSettings = viewModel.isForTestSettings,
        onGroupedTestSettingSelected = viewModel::onGroupedTestSettingSelected,
        onIndependentSettingToggled = viewModel::onIndependentSettingToggled,
        paddingValues = it,
    )
}

@Composable
private fun EditFeatureFlagContent(
    testSettings: Map<Int?, List<FeatureToggle>>,
    isForTestSettings: Boolean = true,
    onGroupedTestSettingSelected: (FeatureToggle) -> Unit = { _ -> },
    onIndependentSettingToggled: (FeatureToggle, Boolean) -> Unit = { _, _ -> },
    paddingValues: PaddingValues = PaddingValues(),
) = LazyColumn(
    modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .navigationBarsPadding()
        .padding(
            start = 16.dp, end = 16.dp
        )
) {
    item {
        Spacer(Modifier.height(24.dp))
        Text(
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            text = if (isForTestSettings) stringResource(R.string.editflags_edit_test_settings) else stringResource(
                R.string.editflags_edit_feature_flags
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    testSettings.keys.forEach { groupId ->
        val groupedSettings = testSettings[groupId]
        val groupName = groupedSettings?.first()?.groupName ?: "TEST FLAGS FOR GROUP $groupId"
        //Test settings that are grouped. Only one setting may be active at a time in the entire group
        if (groupId != null) {
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    text = groupName,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Column(Modifier.selectableGroup()) {
                    groupedSettings?.forEach { testSetting ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 56.dp)
                                .padding(end = 16.dp)
                                .selectable(
                                    selected = testSetting.isEnabled,
                                    onClick = { onGroupedTestSettingSelected(testSetting) },
                                    role = Role.RadioButton
                                ), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(end = 12.dp)
                            ) {
                                Text(
                                    text = testSetting.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = testSetting.explanation,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            RadioButton(
                                selected = testSetting.isEnabled, onClick = null
                            )
                        }
                    }
                }
            }
        } else {
            groupedSettings?.forEach { testSetting ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(end = 12.dp)
                        ) {
                            Text(
                                text = testSetting.title,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = testSetting.explanation,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Switch(modifier = Modifier,
                            checked = testSetting.isEnabled,
                            onCheckedChange = {
                                onIndependentSettingToggled(
                                    testSetting, it
                                )
                            })
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview_SettingsScreen() {
    EduidAppAndroidTheme {
        EditFeatureFlagContent(
            testSettings = groupedTestSetting
        )
    }
}

private val groupedTestSetting = GroupedTestSetting.entries.map { it ->
    FeatureToggle(
        key = it.key,
        groupId = it.groupId,
        groupName = it.groupName,
        title = it.name,
        explanation = it.explanation,
        isEnabled = false
    )
}.groupBy { it.groupId }
private val featureFlags = FeatureFlag.entries.map { it ->
    FeatureToggle(
        key = it.key,
        groupId = null,
        groupName = null,
        title = it.name,
        explanation = it.explanation,
        isEnabled = false
    )
}.groupBy { it.groupId }