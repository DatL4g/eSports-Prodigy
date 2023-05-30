plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.gradle.android.cache-fix") apply false
    kotlin("plugin.serialization")
    id ("kotlin-parcelize") apply false
}

group = "dev.datlag.esports.prodigy.game"

kotlin {
    android()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":model"))
            }
        }
        val androidMain by getting {
            apply(plugin = "org.gradle.android.cache-fix")
            apply(plugin = "kotlin-parcelize")
        }
        val desktopMain by getting
    }
}

android {
    compileSdk = Configuration.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    namespace = "dev.datlag.esports.prodigy.game"

    defaultConfig {
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
    }
}