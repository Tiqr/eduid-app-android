package nl.eduid.flags

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
) : Feature,
    WithClearData,
    WithGroup {
    USE_TEST2_ENV(
        key = "use_test2_env",
        groupId = ENVIRONMENT_GROUP,
        groupName = "Environment",
        title = "Use Test2 Environment",
        explanation = "When on, the app will work with the test2 environment",
        defaultValue = false,
        doRestart = true,
    ),
    USE_TEST_ENV(
        key = "use_test_env",
        groupId = ENVIRONMENT_GROUP,
        groupName = "Environment",
        title = "Use Test Environment",
        explanation = "When on, the app will work with the test environment",
        defaultValue = true,
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
) : Feature

private const val ENVIRONMENT_GROUP = 0