plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
}

kotlin {
    android("android")
    jvm("desktop")

    sourceSets {
        commonMain {
            dependencies {
                api(compose.runtime)
                api(compose.ui)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)
            }
        }
    }
}

android {
    compileSdk = Configuration.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    namespace = "com.netguru.multiplatform.charts"

    defaultConfig {
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
    }

    compileOptions {
        sourceCompatibility = CompileOptions.sourceCompatibility
        targetCompatibility = CompileOptions.targetCompatibility
    }

    packagingOptions {
        resources.excludes.add("META-INF/*")
    }
}