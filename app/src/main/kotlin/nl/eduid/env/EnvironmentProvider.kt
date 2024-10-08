package nl.eduid.env

import nl.eduid.flags.GroupedTestSetting
import nl.eduid.flags.RuntimeBehavior
import javax.inject.Inject

class EnvironmentProvider @Inject constructor(private val runtimeBehavior: RuntimeBehavior) {

    fun getCurrent(): Environment = when {
        runtimeBehavior.isFeatureEnabled(GroupedTestSetting.USE_TEST2_ENV) -> Environment.Test2
        runtimeBehavior.isFeatureEnabled(GroupedTestSetting.USE_TEST_ENV) -> Environment.Test
        runtimeBehavior.isFeatureEnabled(GroupedTestSetting.USE_ACCEPTANCE_ENV) -> Environment.Acceptance
        else -> Environment.Production
    }
}