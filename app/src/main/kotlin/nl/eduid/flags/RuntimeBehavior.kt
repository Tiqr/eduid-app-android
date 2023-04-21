package nl.eduid.flags

import android.content.Context
import nl.eduid.BuildConfig
import java.util.concurrent.CopyOnWriteArrayList

object RuntimeBehavior {

    private val providers = CopyOnWriteArrayList<FeatureFlagProvider>()

    fun initialize(context: Context) {
        if (BuildConfig.DEBUG) {
            addProvider(LocalFeatureFlagProvider(context))
        } else {
            addProvider(StoreFeatureFlagProvider())
        }
    }

    fun isFeatureEnabled(feature: Feature): Boolean {
        return providers.filter { it.hasFeature(feature) }
            .minByOrNull(FeatureFlagProvider::priority)
            ?.isFeatureEnabled(feature)
            ?: feature.defaultValue
    }

    fun getEditableProvider() =
        providers.filterIsInstance(EditableFeatureFlagProvider::class.java).first()

    private fun addProvider(provider: FeatureFlagProvider) {
        providers.add(provider)
    }
}
