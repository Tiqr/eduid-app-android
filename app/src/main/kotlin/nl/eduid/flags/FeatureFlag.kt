package nl.eduid.flags

/**
 * A feature flag is something that disappears over time (hence it is a tool to simplify development) e.g we develop a feature, test it,
 * release it, then we remove it and the feature remain in the app
 */
enum class FeatureFlag(
    override val key: String,
    override val title: String,
    override val explanation: String,
    override val defaultValue: Boolean = false,
) : Feature {
    SHOW_ADD_SECURITY_KEY(
        key = "show_add_security_key",
        title = "Show 'Add a security key' button",
        explanation = "When on, it will show the `Add a security key` on the Security screen",
        defaultValue = false,
    ),
    ENABLE_IDENTITY_VERIFICATION(
        key = "enable_identity_verification",
        title = "Enable Identity Verification",
        explanation = "When on, it will enable the user to verify their identity via a third party",
        defaultValue = true,
    ),
}