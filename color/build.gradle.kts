plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.cache.fix) apply false
}

group = "dev.datlag.esports.prodigy.color"

kotlin {
    android("android")
    jvm()

    sourceSets {
        val commonMain by getting
        val androidMain by getting {
            dependsOn(commonMain)
            apply(plugin = "org.gradle.android.cache-fix")
        }
        val jvmMain by getting {
            dependsOn(commonMain)
        }
    }
}

android {
    compileSdk = Configuration.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    namespace = "dev.datlag.esports.prodigy.color"

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