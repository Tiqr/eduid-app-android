rootProject.name = "eduid app android"

include(":app")

// Add core
include(":core")
project(":core").projectDir = File("app-core/core")

// Add data
include(":data")
project(":data").projectDir = File("app-core/data")


pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
