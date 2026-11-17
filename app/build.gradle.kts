import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleservices)
    alias(libs.plugins.firebase.crashlytics)
}

// Release signing values are never committed. They are read from local.properties
// (git-ignored, for local builds) or from environment variables (CI), using the same names:
//   RELEASE_STORE_FILE      path to the keystore (relative to the project root, or absolute)
//   RELEASE_STORE_PASSWORD
//   RELEASE_KEY_ALIAS
//   RELEASE_KEY_PASSWORD
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

fun signingSecret(name: String): String? =
    (localProperties.getProperty(name) ?: providers.environmentVariable(name).orNull)
        ?.takeIf { it.isNotBlank() }

val releaseStoreFile = signingSecret("RELEASE_STORE_FILE")?.let { rootProject.file(it) }
val releaseStorePassword = signingSecret("RELEASE_STORE_PASSWORD")
val releaseKeyAlias = signingSecret("RELEASE_KEY_ALIAS")
val releaseKeyPassword = signingSecret("RELEASE_KEY_PASSWORD")
val hasReleaseSigning = releaseStoreFile?.exists() == true &&
    releaseStorePassword != null && releaseKeyAlias != null && releaseKeyPassword != null

if (!hasReleaseSigning) {
    logger.warn(
        "Release signing is not configured (RELEASE_STORE_FILE / RELEASE_STORE_PASSWORD / " +
            "RELEASE_KEY_ALIAS / RELEASE_KEY_PASSWORD). Release builds will be unsigned."
    )
}

android {
    namespace = "com.quickmindgames.sudoku"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.quickmindgames.sudoku"
        minSdk = 28
        targetSdk = 36
        versionCode = 13
        versionName = "0.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("quickmind") {
                storeFile = releaseStoreFile
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("quickmind")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    // Ensures Gradle uses JDK 17 toolchain for Kotlin compilation
    jvmToolchain(17)

    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.fragment)
    implementation(libs.lottie.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // For icons
    implementation(libs.androidx.compose.material.icons.extended)

    // For navigation
    implementation(libs.androidx.navigation.compose)

    // DataStore for persisting game state
    implementation(libs.androidx.datastore.preferences)

    // Room database with KSP
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // WorkManager for background scheduling and notifications
    implementation(libs.androidx.work.runtime.ktx)

    // firebase for analytics and crashlytics
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Remote Config for dynamic feature flags
    implementation(libs.firebase.remote.config)

    // Splash Screen
    implementation(libs.splash.screen)
}