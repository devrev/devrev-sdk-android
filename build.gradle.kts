buildscript {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.android.gradlePlugin)
        classpath("com.google.gms:google-services:4.3.8")
    }
}

plugins {
    id("com.google.gms.google-services") version "4.3.8" apply false
}

