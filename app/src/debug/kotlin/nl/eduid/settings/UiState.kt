package nl.eduid.settings

data class UiState(
    val featureFlagsData: Map<Int?, List<FeatureToggle>> = emptyMap(),
    val isProcessing: Boolean = false,
    val isDone: Unit? = null,
    val showForceStopPrompt: Unit? = null,
)

data class FeatureToggle(
    val key: String,
    val groupId: Int?,
    val groupName: String?,
    val title: String,
    val explanation: String,
    val isEnabled: Boolean,
) {
    val isGroupedFlag = groupId != null
}