plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.cache.fix) apply false
    alias(libs.plugins.serialization)
    id ("kotlin-parcelize") apply false
}

group = "dev.datlag.esports.prodigy.game"

kotlin {
    android("android")
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":model"))
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            apply(plugin = "org.gradle.android.cache-fix")
            apply(plugin = "kotlin-parcelize")
        }
        val desktopMain by getting {
            dependsOn(commonMain)
            dependencies {
                api(libs.lang)
            }
        }
    }
}

android {
    compileSdk = Configuration.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    namespace = "dev.datlag.esports.prodigy.game"

    defaultConfig {
        minSdk = Configuration.minSdk
    }

    compileOptions {
        sourceCompatibility = CompileOptions.sourceCompatibility
        targetCompatibility = CompileOptions.targetCompatibility
    }
}