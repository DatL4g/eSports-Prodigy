plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.cache.fix) apply false
    id ("kotlin-parcelize") apply false
}

group = "dev.datlag.esports.prodigy.model"

kotlin {
    jvm()
    androidTarget("android")

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.parcelable)
                api(libs.serialization.json)
                api(libs.serialization.xml)
                api(libs.coroutines)
            }
        }

        val jvmMain by getting {
            dependsOn(commonMain)
        }

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
    }

    compileOptions {
        sourceCompatibility = CompileOptions.sourceCompatibility
        targetCompatibility = CompileOptions.targetCompatibility
    }
}