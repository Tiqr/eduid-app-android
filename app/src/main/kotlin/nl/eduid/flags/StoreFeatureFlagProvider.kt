package nl.eduid.flags

class StoreFeatureFlagProvider : FeatureFlagProvider {

    override val priority = MIN_PRIORITY

    /*
     * Do not add an "else" branch here -> choosing the default option for release must be an explicit choice
     */
    @Suppress("ComplexMethod")
    override fun isFeatureEnabled(feature: Feature): Boolean {
        return if (feature is FeatureFlag) {
            false
        } else {
            when (feature as GroupedTestSetting) {
                else -> false
            }
        }
    }

    override fun hasFeature(feature: Feature): Boolean = true
}
