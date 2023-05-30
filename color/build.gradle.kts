plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.gradle.android.cache-fix") apply false
}

group = "dev.datlag.esports.prodigy.color"

kotlin {
    android()
    jvm()

    sourceSets {
        val commonMain by getting
        val androidMain by getting {
            apply(plugin = "org.gradle.android.cache-fix")
        }
        val jvmMain by getting
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    namespace = "dev.datlag.esports.prodigy.color"

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}