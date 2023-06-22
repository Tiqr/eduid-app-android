package nl.eduid.flags

/**
 * A Feature uniquely identifies a part of the app code that can either be enabled or disabled.
 * Features only have two states by design to simplify the implementation
 *
 * @param[key] unique value that identifies a test setting
 */
interface Feature {
    val key: String
    val title: String
    val explanation: String
    val defaultValue: Boolean
}

/**
 * Feature flags may be grouped when optional [groupId] is not null. When a feature flag belongs to
 * a group it means only one flag *per group* may be active at any given time.
 * */
interface WithGroup {
    val groupId: Int
    val groupName: String
}

interface WithClearData {
    val doRestart: Boolean
}

/**
 * A feature flag is something that disappears over time (hence it is a tool to simplify development)
 * e.g we develop a feature, test it, release it, then we remove it and the feature remain in the app
 */
enum class FeatureFlag(
    override val key: String,
    override val title: String,
    override val explanation: String,
    override val defaultValue: Boolean = false,
) : Feature {}

/**
 * A test setting is something that stays in our app forever (hence it is a tool to simplify testing)
 * e.g. it is a hook into our app to allow something that a production app shouldn’t allow. (changing the environment it works with)
 */
enum class GroupedTestSetting(
    override val key: String,
    override val title: String,
    override val groupId: Int,
    override val groupName: String,
    override val explanation: String,
    override val defaultValue: Boolean = false,
    override val doRestart: Boolean = false,
) : Feature, WithClearData, WithGroup {
    USE_TEST2_ENV(
        key = "use_test2_env",
        groupId = ENVIRONMENT_GROUP,
        groupName = "Environment",
        title = "Use Test2 Environment",
        explanation = "When on, the app will work with the test2 environment",
        defaultValue = true,
        doRestart = true,
    ),
    USE_TEST_ENV(
        key = "use_test_env",
        groupId = ENVIRONMENT_GROUP,
        groupName = "Environment",
        title = "Use Test Environment",
        explanation = "When on, the app will work with the test environment",
        defaultValue = false,
        doRestart = true,
    ),
    USE_ACCEPTANCE_ENV(
        key = "use_acceptance_env",
        groupId = ENVIRONMENT_GROUP,
        groupName = "Environment",
        title = "Use Acceptance Environment",
        explanation = "When on, the app will work with the acceptance environment",
        defaultValue = false,
        doRestart = true,
    ),
    USE_PRODUCTION_ENV(
        key = "use_production_env",
        groupId = ENVIRONMENT_GROUP,
        groupName = "Environment",
        title = "Use Production Environment",
        explanation = "When on, the app will work with the production environment",
        defaultValue = false,
        doRestart = true,
    ),
}

enum class TestSettings(
    override val key: String,
    override val title: String,
    override val explanation: String,
    override val defaultValue: Boolean = false,
) : Feature {
    DUMMY_FEATURE_FLAG(
        key = "aaa",
        title = "blabla1",
        explanation = "asdklfjhdsakjfhdsjkafgh",
        defaultValue = false
    ),
    DUMMY_FEATURE_FLAG1(
        key = "dfdf",
        title = "blabla1",
        explanation = "asdklfjhdsakjfhdsjkafgh",
        defaultValue = false
    ),
    DUMMY_FEATURE_FLAG2(
        key = "fghfg",
        title = "blabla1",
        explanation = "asdklfjhdsakjfhdsjkafgh",
        defaultValue = false
    ),
    DUMMY_FEATURE_FLAG3(
        key = "lkjh",
        title = "blabla1",
        explanation = "asdklfjhdsakjfhdsjkafgh",
        defaultValue = false
    ),

}

private const val ENVIRONMENT_GROUP = 0