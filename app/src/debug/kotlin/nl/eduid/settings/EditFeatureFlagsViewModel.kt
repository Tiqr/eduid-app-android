package nl.eduid.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.di.repository.StorageRepository
import nl.eduid.flags.Feature
import nl.eduid.flags.FeatureFlag
import nl.eduid.flags.GroupedTestSetting
import nl.eduid.flags.RuntimeBehavior
import nl.eduid.flags.WithClearData
import nl.eduid.flags.WithGroup
import nl.eduid.graph.FlagRoute
import org.tiqr.data.repository.IdentityRepository
import org.tiqr.data.service.DatabaseService
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditFeatureFlagsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val storage: StorageRepository,
    private val identity: IdentityRepository,
    private val db: DatabaseService,
    private val runtimeBehavior: RuntimeBehavior,
) : ViewModel() {
    val editableProvider = runtimeBehavior.getEditableProvider()
    var uiState by mutableStateOf(UiState())
        private set

    private val flagsToEdit: List<Feature>
    val isForTestSettings: Boolean

    init {
        isForTestSettings =
            savedStateHandle.get<Boolean>(FlagRoute.EditFeatureFlags.isTestSettings) ?: true
        flagsToEdit = if (isForTestSettings) {
            GroupedTestSetting.values().asList()
        } else {
            FeatureFlag.values().asList()
        }
        viewModelScope.launch {
            uiState = uiState.copy(isProcessing = true)
            loadFlagsFromStorage()
        }
    }

    private fun loadFlagsFromStorage(showForceStopPrompt: Unit? = null) {
        val featureFlagData = flagsToEdit.map { it ->
            val groupId = if (it is WithGroup) {
                it.groupId
            } else {
                null
            }
            val groupName = if (it is WithGroup) {
                it.groupName
            } else {
                null
            }
            FeatureToggle(
                key = it.key,
                groupId = groupId,
                groupName = groupName,
                title = it.title,
                explanation = it.explanation,
                isEnabled = editableProvider.isFeatureEnabled(it)
            )
        }.groupBy { it.groupId }
        uiState = uiState.copy(
            isProcessing = false,
            featureFlagsData = featureFlagData,
            showForceStopPrompt = showForceStopPrompt
        )
    }

    fun onGroupedTestSettingSelected(featureToggle: FeatureToggle) = viewModelScope.launch {
        val doRestart =
            flagsToEdit.filter { flag -> featureToggle.groupId != null && flag is WithGroup && flag.groupId == featureToggle.groupId }
                .map { flag ->
                    if (featureToggle.key == flag.key) {
                        editableProvider.setFeatureEnabled(flag, true)
                    } else {
                        editableProvider.setFeatureEnabled(flag, false)
                    }
                    flag
                }.any { flag -> flag is WithClearData }
        val promptRestart = if (doRestart) {
            clearEnvironmentAppData()
            Unit
        } else {
            null
        }
        loadFlagsFromStorage(promptRestart)
    }

    private suspend fun clearEnvironmentAppData() {
        val allIdentities = db.getAllIdentities()
        storage.clearAll()
        try {
            allIdentities.forEach {
                identity.delete(it)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to cleanup existing identities when deleting account")
        }
    }

    fun onIndependentSettingToggled(
        testSetting: FeatureToggle,
        newValue: Boolean,
    ) = viewModelScope.launch {
        uiState = uiState.copy(isProcessing = true)
        val flag = flagsToEdit.first { it.key == testSetting.key }
        editableProvider.setFeatureEnabled(flag, newValue)
        val promptRestart = if (flag is WithClearData) {
            clearEnvironmentAppData()
            Unit
        } else {
            null
        }

        loadFlagsFromStorage(promptRestart)
    }

    fun clearForceStopPrompt() {
        uiState = uiState.copy(showForceStopPrompt = null)
    }

}

