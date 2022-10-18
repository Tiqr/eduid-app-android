plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
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

android {
    compileSdk = libs.versions.android.sdk.compile.get().toInt()
    buildToolsVersion = libs.versions.android.buildTools.get()

    val gitTagCount = "git tag --list".runCommand().split('\n').size
    val gitTag = "git describe --tags --dirty".runCommand()

    defaultConfig {
        applicationId = "nl.eduid"
        val versionCode = gitTagCount.toInt()
        val versionName = gitTag.toString().trim().drop(1)

	val pattern = Regex("^[0-9]\\d*\\.[0-9]\\d*\\.[0-9]\\d*(\\-.*)?")
	if (!pattern.containsMatchIn(versionName)) {
		throw StopExecutionException("Invalid version name.")
	}

        minSdk = libs.versions.android.sdk.min.get().toInt()
        targetSdk = libs.versions.android.sdk.target.get().toInt()

        logger.lifecycle("Building version "+versionName+"("+versionCode+")", "info")

        testInstrumentationRunner = "nl.eduid.runner.HiltAndroidTestRunner"

        manifestPlaceholders["tiqr_config_base_url"] = "https://demo.tiqr.org"
        manifestPlaceholders["tiqr_config_protocol_version"] = "2"
        manifestPlaceholders["tiqr_config_protocol_compatibility_mode"] =  "true"
        manifestPlaceholders["tiqr_config_enforce_challenge_hosts"] = "eduid.nl,surfconext.nl"
        manifestPlaceholders["tiqr_config_enroll_path_param"] = "tiqrenroll"
        manifestPlaceholders["tiqr_config_auth_path_param"] = "tiqrauth"
        manifestPlaceholders["tiqr_config_enroll_scheme"] = "eduidenroll"
        manifestPlaceholders["tiqr_config_auth_scheme"] = "eduidauth"
        manifestPlaceholders["tiqr_config_token_exchange_enabled"] = "false"

        // only package supported languages
        resourceConfigurations += listOf("en", "nl")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    flavorDimensions.add("releaseType")
    productFlavors {
        create("acceptance") {
            dimension = "releaseType"
            applicationIdSuffix = ".testing"
        }
        create("production") {
            dimension = "releaseType"
        }
    }

    buildFeatures {
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kapt {
        correctErrorTypes = true
        useBuildCache = true
        javacOptions {
            option("-Xmaxerrs", 1000)
        }
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    lint {
        isAbortOnError = false
    }
}

dependencies {

    repositories {
        google()
        mavenCentral()
    }

    implementation(project(":data"))
    implementation(project(":core"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.core)
    implementation(libs.kotlinx.coroutines.playServices)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.autofill)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core)
    implementation(libs.androidx.concurrent)
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

    implementation(libs.dagger.hilt.android)
    implementation(libs.dagger.hilt.fragment)
    kapt(libs.dagger.hilt.compiler)

    implementation(libs.permission)
    implementation(libs.coil)
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
