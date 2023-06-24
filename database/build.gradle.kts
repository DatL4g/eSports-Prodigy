plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.sqldelight")
}

group = "dev.datlag.esports.prodigy.database"

kotlin {
    android()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.squareup.sqldelight:coroutines-extensions:1.5.5")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:android-driver:1.5.5")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:sqlite-driver:1.5.5")
            }
        }
    }
}

android {
    sourceSets["main"].setRoot("src/androidMain/")
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    compileSdk = Configuration.compileSdk
    buildToolsVersion = Configuration.buildTools

    namespace = "dev.datlag.esports.prodigy.database"

    defaultConfig {
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
    }

    compileOptions {
        sourceCompatibility = CompileOptions.sourceCompatibility
        targetCompatibility = CompileOptions.targetCompatibility
    }
}

sqldelight {
    database("HLTVDB") {
        packageName = "dev.datlag.esports.prodigy.database"
        sourceFolders = listOf("hltv")
    }
    database("CounterStrikeDB") {
        packageName = "dev.datlag.esports.prodigy.database"
        sourceFolders = listOf("user")
    }
}