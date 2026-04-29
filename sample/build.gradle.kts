plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services") version "4.4.2"
}

apply(from = "$projectDir/dependencies.gradle.kts")
val versions: Map<String, String> by extra
val cameraxVersion = "1.3.0"


android {
    namespace = "ai.devrev.sdk.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "ai.devrev.sdk.sample"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}


dependencies {
    implementation(versions["androidx.core"]!!)
    implementation(versions["androidx.appcompat"]!!)
    implementation(versions["com.google.android.material"]!!)
    implementation(versions["androidx.constraintlayout"]!!)
    implementation(versions["ai.devrev.sdk"]!!)
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("androidx.activity:activity-ktx:1.2.3")
    implementation("androidx.activity:activity-compose:1.6.0")
    implementation("androidx.compose.material3:material3-android:1.3.1")
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))
    implementation("com.google.firebase:firebase-messaging:23.0.0")
    implementation("androidx.compose.ui:ui:1.3.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.3.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.3.0")
    implementation("androidx.compose.material:material:1.3.0")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("androidx.compose.runtime:runtime-livedata:1.3.0")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.google.guava:guava:31.1-android")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.patrykandpatrick.vico:compose:1.13.1")
    implementation("com.patrykandpatrick.vico:compose-m3:1.13.1")
    implementation("com.patrykandpatrick.vico:core:1.13.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("com.airbnb.android:lottie-compose:6.0.0")
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
}
