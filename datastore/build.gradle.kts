plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.gradle.android.cache-fix") apply false
}

group = "dev.datlag.esports.prodigy.datastore"

kotlin {
    android()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("androidx.datastore:datastore-core:1.0.0")
                api(project("proto"))
                implementation(project(":model"))
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

    namespace = "dev.datlag.esports.prodigy.datastore"

    defaultConfig {
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
    }
}
