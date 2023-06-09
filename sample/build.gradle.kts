plugins {
    id("com.android.application")
    kotlin("android")
}

apply(from = "$projectDir/dependencies.gradle.kts")
val versions: Map<String, String> by extra

android {
    namespace = "ai.devrev.sdk.sample"
    compileSdk = 33

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
        jvmTarget = "1.8"
    }
}


dependencies {
    implementation(versions["androidx.core"]!!)
    implementation(versions["androidx.appcompat"]!!)
    implementation(versions["com.google.android.material"]!!)
    implementation(versions["androidx.constraintlayout"]!!)
    implementation(versions["ai.devrev.sdk"]!!)
    testImplementation(versions["junit"]!!)
    androidTestImplementation(versions["androidx.test.ext"]!!)
    androidTestImplementation(versions["androidx.test.espresso"]!!)
}
