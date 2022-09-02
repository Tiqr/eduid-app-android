rootProject.name = "eduid app android"

include(":app")

// Add core
include(":core")
project(":core").projectDir = File("app-core/core")

// Add data
include(":data")
project(":data").projectDir = File("app-core/data")

// Enable Gradle's version catalog support
// https://docs.gradle.org/current/userguide/platforms.html
enableFeaturePreview("VERSION_CATALOGS")
