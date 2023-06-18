plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.android.library")
    id("org.gradle.android.cache-fix") apply false
}

group = "dev.datlag.esports.prodigy.network"

val flower = "3.0.0"
val ktorfit = "1.4.1"
val ktor = "2.3.1"

kotlin {
    jvm()
    android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":model"))
                implementation("io.github.hadiyarajesh.flower-core:flower:$flower")
                api("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfit")
                implementation("it.skrape:skrapeit:1.1.5")
                api("io.ktor:ktor-client-okhttp:$ktor")
                api("io.ktor:ktor-client-content-negotiation:$ktor")
                api("io.ktor:ktor-serialization-kotlinx-json:$ktor")
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
}

dependencies {
    add("kspCommonMainMetadata", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfit")
    add("kspJvm", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfit")
    add("kspAndroid", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfit")
}
