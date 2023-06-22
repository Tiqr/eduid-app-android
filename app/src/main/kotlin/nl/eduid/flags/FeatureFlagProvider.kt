package nl.eduid.flags

/**
 * Every provider has an explicit priority so they can override each other (e.g. "Remote Config tool" > Store).
 *
 * Not every provider has to provide a flag value for every feature. This is to avoid implicitly relying on build-in
 * defaults (e.g. "Remote Config tool" returns false when no value for a feature) and to avoid that every provider has to provide a
 * value for every feature. (e.g. no "Remote Config tool" configuration needed, unless you want the toggle to be remote)
 */
interface FeatureFlagProvider {

    val priority: Int
    fun isFeatureEnabled(feature: Feature): Boolean
    fun hasFeature(feature: Feature): Boolean
}

/**
 * Interface to be implemented by the platform specific `FeatureFlagProvider` that is backed by local storage.
 * These flags can be edited by each platform via a debug settings screen.
 * */
interface EditableFeatureFlagProvider : FeatureFlagProvider {
    fun setFeatureEnabled(feature: Feature, enabled: Boolean)
}

const val MAX_PRIORITY = 1
const val MEDIUM_PRIORITY = 2
const val MIN_PRIORITY = 3
