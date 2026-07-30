plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleservices)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.sonarqube)
}

android {
    namespace = "com.quickmindgames.sudoku"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.quickmindgames.sudoku"
        minSdk = 28
        targetSdk = 36
        versionCode = 11
        versionName = "0.1.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("quickmind") {
            keyAlias = "quickmindgames"
            keyPassword = "QMG#126gma"
            storeFile = file("../release key/quickmind.jks")
            storePassword = "QMG#126gma"
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
            signingConfig = signingConfigs.getByName("quickmind")
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
    sonarqube {
        properties {
            property("sonar.projectKey", "QMG_Sudoku")
            property("sonar.projectName", "QMG_Sudoku")
            property("sonar.host.url", "http://localhost:9000")
            property("sonar.token", "sqp_ddd475b0a749cedebd2516f23400cc8d3927fcfb")
            property("sonar.language", "kotlin")
            property("sonar.sourceEncoding", "UTF-8")
            property("sonar.android.resourceFiles.skip", "true")
        }
    }
    tasks.matching { it.name == "sonarResolver" }.configureEach {
        dependsOn(tasks.matching { it.name.contains("generate") && it.name.contains("ResValues") })
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
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
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
}