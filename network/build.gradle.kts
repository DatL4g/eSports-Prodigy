plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.cache.fix) apply false
}

group = "dev.datlag.esports.prodigy.network"

kotlin {
    jvm()
    android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":model"))

                implementation(libs.flower)
                api(libs.ktorfit)
                implementation(libs.skrapeit)
                api(libs.ktor.okhttp)
                api(libs.ktor.content.negotiation)
                api(libs.ktor.serialization.json)
                api(libs.ktor.serialization.xml)
                api(libs.datetime)
            }
        }

        val jvmMain by getting

        val androidMain by getting {
            apply(plugin = "org.gradle.android.cache-fix")
        }
    }
}

android {
    compileSdk = Configuration.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    namespace = "dev.datlag.esports.prodigy.network"

    defaultConfig {
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
    }

    compileOptions {
        sourceCompatibility = CompileOptions.sourceCompatibility
        targetCompatibility = CompileOptions.targetCompatibility
    }
}

dependencies {
    val ktorfit = libs.versions.ktorfit.asProvider().get()

    add("kspCommonMainMetadata", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfit")
    add("kspJvm", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfit")
    add("kspAndroid", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfit")
}
