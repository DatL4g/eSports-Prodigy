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
            apply(plugin = "org.gradle.android.cache-fix")
        }
        val jvmMain by getting
    }
}

android {
    compileSdk = Configuration.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    namespace = "dev.datlag.esports.prodigy.color"

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