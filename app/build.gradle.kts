plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
}

if (JavaVersion.current() < JavaVersion.VERSION_11) {
    throw GradleException("Please use JDK ${JavaVersion.VERSION_11} or above")
}

fun String.runCommand(workingDir: File = file("./")): String {
    val parts = this.split("\\s".toRegex())
    val proc = ProcessBuilder(*parts.toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
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

android {
    compileSdk = libs.versions.android.sdk.compile.get().toInt()
    buildToolsVersion = libs.versions.android.buildTools.get()

    val gitTagCount = "git tag --list".runCommand().split('\n').size
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
        versionCode = gitTagCount
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
        getByName("debug") {
            storeFile = file("keystore/testing.keystore")
            storePassword = keystorePass
            keyAlias = "androiddebugkey"
            keyPassword = keystorePass
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            buildConfigField("String", "ENV_HOST", "\"https://login.test2.eduid.nl\"")
            buildConfigField("String", "CLIENT_ID", "\"\"")
        }

        getByName("debug") {
            applicationIdSuffix = ".testing"
            versionNameSuffix = " DEBUG"
            buildConfigField("String", "ENV_HOST", "\"https://login.test2.eduid.nl\"")
            buildConfigField("String", "CLIENT_ID", "\"dev.egeniq.nl\"")
        }
    }

    buildFeatures {
        dataBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    kapt {
        correctErrorTypes = true
        useBuildCache = true
        javacOptions {
            option("-Xmaxerrs", 1000)
        }
    }

    lint {
        abortOnError = false
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.5"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "nl.eduid"
}

dependencies {

    implementation(project(":data"))
    implementation(project(":core"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.core)
    implementation(libs.kotlinx.coroutines.playServices)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.autofill)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.biometric)
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.runtime:runtime-livedata")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.compose.hilt.navigation)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.compose.constraint)
    implementation(libs.androidx.core)
    implementation(libs.androidx.concurrent)
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
    implementation(libs.google.mlkit.barcode)
    implementation(libs.google.firebase.messaging)
    implementation(libs.appauth)
    implementation(libs.jwtdecode)

    implementation(libs.dagger.hilt.android)
    implementation(libs.dagger.hilt.fragment)
    kapt(libs.dagger.hilt.compiler)

    implementation(libs.permission)
    implementation(libs.coil)
    implementation(libs.coilCompose)
    implementation(libs.betterLink)

    api(libs.moshi.moshi)
    kapt(libs.moshi.codegen)

    api(libs.okhttp.okhttp)
    api(libs.okhttp.logging)

    api(libs.retrofit.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.converter.scalars)

    api(libs.timber)

    testImplementation(libs.junit)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.testing.core)
    androidTestImplementation(libs.androidx.testing.junit)
    androidTestImplementation(libs.androidx.testing.rules)
    androidTestImplementation(libs.androidx.testing.epsresso)
    androidTestImplementation(libs.androidx.testing.uiautomator)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.dagger.hilt.testing)
    kaptAndroidTest(libs.dagger.hilt.compiler)
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
