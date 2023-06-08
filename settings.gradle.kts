pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
    val pluginSerialization: String by settings
    plugins {
        kotlin("plugin.serialization") version pluginSerialization
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}
rootProject.name = "DevRev Android Sample"
include(":sample")
