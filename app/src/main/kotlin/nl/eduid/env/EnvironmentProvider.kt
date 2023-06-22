package nl.eduid.env

import nl.eduid.flags.RuntimeBehavior
import nl.eduid.flags.GroupedTestSetting

object EnvironmentProvider {

    fun getCurrent(): Environment = when {
        RuntimeBehavior.isFeatureEnabled(GroupedTestSetting.USE_TEST2_ENV) -> Environment.Test2
        RuntimeBehavior.isFeatureEnabled(GroupedTestSetting.USE_TEST_ENV) -> Environment.Test
        RuntimeBehavior.isFeatureEnabled(GroupedTestSetting.USE_ACCEPTANCE_ENV) -> Environment.Acceptance
        else -> Environment.Production
    }
}