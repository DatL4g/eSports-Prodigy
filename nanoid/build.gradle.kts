plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.gradle.android.cache-fix") apply false
}

group = "dev.datlag.esports.prodigy.nanoid"

kotlin {
    android()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.kotlincrypto:secure-random:0.1.0")
            }
        }
        val androidMain by getting {
            apply(plugin = "org.gradle.android.cache-fix")
        }
        val desktopMain by getting
    }
}

android {
    compileSdk = Configuration.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    namespace = "dev.datlag.esports.prodigy.nanoid"

    defaultConfig {
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
    }
}
