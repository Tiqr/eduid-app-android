package nl.eduid.flags

import android.content.Context
import android.content.SharedPreferences

class LocalFeatureFlagProvider(context: Context) :
    EditableFeatureFlagProvider {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        "runtime.featureflags",
        Context.MODE_PRIVATE
    )

    override val priority = MEDIUM_PRIORITY

    override fun isFeatureEnabled(feature: Feature): Boolean =
        preferences.getBoolean(feature.key, feature.defaultValue)

    override fun hasFeature(feature: Feature): Boolean = true

    override fun setFeatureEnabled(feature: Feature, enabled: Boolean) =
        preferences.edit().putBoolean(feature.key, enabled).apply()

}