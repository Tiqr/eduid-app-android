package nl.eduid.flags

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import nl.eduid.BuildConfig
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject


class RuntimeBehavior @Inject constructor(@ApplicationContext context: Context) {

    private val providers = CopyOnWriteArrayList<FeatureFlagProvider>()

    init {
        if (BuildConfig.BUILD_TYPE == "debug") {
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
        providers.filterIsInstance<EditableFeatureFlagProvider>().first()

    private fun addProvider(provider: FeatureFlagProvider) {
        providers.add(provider)
    }
}
