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

