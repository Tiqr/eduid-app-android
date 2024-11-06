plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

if (JavaVersion.current() < JavaVersion.VERSION_17) {
    throw GradleException("Please use JDK ${JavaVersion.VERSION_17} or above")
}

fun String.runCommand(workingDir: File = file("./")): String {
    val parts = this.split("\\s".toRegex())
    val proc = ProcessBuilder(*parts.toTypedArray()).directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE).redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    proc.waitFor(1, TimeUnit.MINUTES)
    return proc.inputStream.bufferedReader().readText().trim()
}

val devKeystorePassFile = File("${projectDir}/keystore/keystorepass.txt")
val keystorePass = if (devKeystorePassFile.exists()) {
    devKeystorePassFile.readText().trim()
} else {
    //Used by the github action
    System.getenv("ANDROID_KEYSTORE_PASSWORD")
}
//We want to have the testing app with editable feature flags uploaded to Google Play.
//Apps uploaded to google play must not be debuggable, hence the flag:
val isAppDebuggable = System.getenv("CI") != "true"

android {
    compileSdk = libs.versions.android.sdk.compile.get().toInt()

    val gitCommitCount = "git rev-list --all --count".runCommand().toInt()
    val gitTag = "git describe --tags --dirty".runCommand()
    val gitCoreSha = "git submodule status".runCommand().substring(0, 8)

    defaultConfig {
        manifestPlaceholders += mapOf(
            "tiqr_config_base_url" to "https://demo.tiqr.org",
            "tiqr_config_protocol_version" to "2",
            "tiqr_config_protocol_compatibility_mode" to "true",
            "tiqr_config_enforce_challenge_hosts" to "eduid.nl,surfconext.nl",
            "tiqr_config_enroll_path_param" to "tiqrenroll",
            "tiqr_config_auth_path_param" to "tiqrauth",
            "tiqr_config_enroll_scheme" to "eduidenroll",
            "tiqr_config_auth_scheme" to "eduidauth",
            "tiqr_config_token_exchange_enabled" to "false",
            "appAuthRedirectScheme" to "eduid"
        )
        applicationId = "nl.eduid"
        versionCode = gitCommitCount
        versionName = gitTag.trim().drop(1) + " core($gitCoreSha)"

        minSdk = libs.versions.android.sdk.min.get().toInt()
        targetSdk = libs.versions.android.sdk.target.get().toInt()

        testInstrumentationRunner = "nl.eduid.runner.HiltAndroidTestRunner"

        // only package supported languages
        resourceConfigurations += listOf("en", "nl")
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        //Must use a unified debug signing certificate, otherwise deep linking verification will fail on Android>=12
        //Only used for signing debuggable builds when building locally or apks from PRs that are archived
        //Must not be used for signing when building a bundle for Google Play upload
        if (isAppDebuggable) {
            getByName("debug") {
                storeFile = file("keystore/testing.keystore")
                storePassword = keystorePass
                keyAlias = "androiddebugkey"
                keyPassword = keystorePass
            }
        } else {
            println("SKIPPING debug signing")
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
            applicationIdSuffix = ".testing"
            versionNameSuffix = if (isAppDebuggable) {
                " DEBUG"
            } else {
                " TESTING"
            }
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            isDebuggable = isAppDebuggable
            signingConfig = if (isAppDebuggable) {
                signingConfigs.getByName("debug")
            } else {
                null
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    lint {
        abortOnError = false
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources.excludes.addAll(
            arrayOf(
                "/META-INF/AL2.0",
                "/META-INF/LGPL2.1",
            )
        )
    }
    namespace = "nl.eduid"
}

dependencies {

    implementation(project(":data"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.core)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.autofill)
    implementation(libs.androidx.lifecycle.process)
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.runtime:runtime-livedata")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation(libs.rebugger)
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.compose.hilt.navigation)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.compose.constraint)
    implementation(libs.androidx.core)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.localBroadcastManager)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.splashscreen)
    implementation(libs.google.android.material)
    implementation(libs.google.firebase.messaging)
    implementation(libs.appauth)
    implementation(libs.jwtdecode)
    implementation(libs.material3.html.text)

    implementation(libs.androidx.camera.camera2)

    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.permission)
    implementation(libs.coil)
    implementation(libs.coilCompose)
    implementation(libs.coilSvg)
    implementation(libs.betterLink)

    ksp(libs.moshi.codegen)

    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.converter.scalars)
}

// Disable analytics
configurations {
    all {
        exclude(group = "com.google.firebase", module = "firebase-core")
        exclude(group = "com.google.firebase", module = "firebase-analytics")
        exclude(group = "com.google.firebase", module = "firebase-measurement-connector")
    }
}

apply {
    plugin("com.google.gms.google-services")
}
