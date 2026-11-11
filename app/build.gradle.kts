plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "tech.sidi.terx001"
    compileSdk = 35

    defaultConfig {
        applicationId = "tech.sidi.terx001"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0-spike"
        // Spike: arm64 only to keep the APK small. Almost all current phones are arm64.
        ndk { abiFilters += listOf("arm64-v8a") }
    }

    signingConfigs {
        // Shared DEBUG key (not a secret) so every CI build installs over the previous one.
        // The release key will be separate and kept in GitHub secrets.
        getByName("debug") {
            storeFile = file("debug-shared.p12")
            storeType = "pkcs12"
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // TEMPORARY: signed with the shared debug key for testing only.
            // A separate real release key (kept in GitHub secrets) comes before any public release.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }

    // youtubedl-android needs its bundled python/yt-dlp libs extracted to disk.
    packaging { jniLibs { useLegacyPackaging = true } }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("io.github.junkfood02.youtubedl-android:library:0.18.1")
}
