plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id ("kotlin-parcelize") apply false
    id("org.gradle.android.cache-fix") apply false
}

group = "dev.datlag.esports.prodigy.model"

kotlin {
    jvm()
    android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.arkivanov.essenty:parcelable:1.1.0")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
            }
        }

        val jvmMain by getting

        val androidMain by getting {
            dependsOn(jvmMain)
            apply(plugin = "kotlin-parcelize")
            apply(plugin = "org.gradle.android.cache-fix")
        }
    }
}

android {
    compileSdk = Configuration.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    namespace = "dev.datlag.esports.prodigy.model"

    defaultConfig {
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
    }

    compileOptions {
        sourceCompatibility = CompileOptions.sourceCompatibility
        targetCompatibility = CompileOptions.targetCompatibility
    }
}