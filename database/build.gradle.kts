plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.sqldelight)
}

group = "dev.datlag.esports.prodigy.database"

kotlin {
    android("android")
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.sqldelight.coroutines)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.sqldelight.android)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.sqldelight.sqlite)
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
    databases {
        create("HLTVDB") {
            packageName.set("dev.datlag.esports.prodigy.database")
            srcDirs("src/commonMain/hltv")
        }
        create("CounterStrikeDB") {
            packageName.set("dev.datlag.esports.prodigy.database")
            srcDirs(listOf("src/commonMain/user"))
        }
    }
}