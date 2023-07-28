plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.cache.fix) apply false
}

group = "dev.datlag.esports.prodigy.nanoid"

kotlin {
    android("android")
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.krypto)
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            apply(plugin = "org.gradle.android.cache-fix")
        }
        val desktopMain by getting {
            dependsOn(commonMain)
        }
    }
}

android {
    compileSdk = Configuration.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    namespace = "dev.datlag.esports.prodigy.nanoid"

    defaultConfig {
        minSdk = Configuration.minSdk
    }

    compileOptions {
        sourceCompatibility = CompileOptions.sourceCompatibility
        targetCompatibility = CompileOptions.targetCompatibility
    }

    packaging {
        resources.excludes.add("META-INF/*")
    }
}
