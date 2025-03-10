plugins {
    id("com.android.application")
    kotlin("android")
    alias(libs.plugins.google.gms.google.services)
}

apply(from = "$projectDir/dependencies.gradle.kts")
val versions: Map<String, String> by extra

android {
    namespace = "ai.devrev.sdk.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "ai.devrev.sdk.sample"
        minSdk = 29
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
        kotlinCompilerExtensionVersion = "1.3.2"
    }
}


dependencies {
    implementation(versions["androidx.core"]!!)
    implementation(versions["androidx.appcompat"]!!)
    implementation(versions["com.google.android.material"]!!)
    implementation(versions["androidx.constraintlayout"]!!)
    implementation(versions["ai.devrev.sdk"]!!)
    implementation(libs.android.material)
    implementation(libs.androidx.appcompat)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.activity)
    implementation(libs.androidx.material3.android)
    testImplementation(versions["junit"]!!)
    androidTestImplementation(versions["androidx.test.ext"]!!)
    androidTestImplementation(versions["androidx.test.espresso"]!!)
    implementation(platform(libs.firebaseBom))
    implementation("com.google.firebase:firebase-messaging:23.0.0")
    implementation("com.google.firebase:firebase-analytics-ktx:21.0.0")
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}
