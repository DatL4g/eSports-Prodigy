import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("com.android.application")
    id("kotlin-parcelize") apply false
    id("com.mikepenz.aboutlibraries.plugin")
    id("de.jensklingenberg.ktorfit")
    id("com.google.osdetector")
    id("dev.icerock.mobile.multiplatform-resources")
    id("org.gradle.android.cache-fix") apply false
}

val coroutines = "1.7.1"
val decompose = "1.0.0"
val kodein = "7.20.1"
val ktor = "2.3.0"
val moko = "0.22.0"
val kamel = "0.5.0"
val napier = "2.6.1"

val artifact = "dev.datlag.esports.prodigy"
val appVersion = "1.0.0"
val appCode = 100

group = artifact
version = appVersion

kotlin {
    android {
        jvmToolchain(11)
    }
    jvm("desktop") {
        jvmToolchain(11)
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.material3)
                api(compose.materialIconsExtended)

                api(kotlin("stdlib"))

                api("com.arkivanov.decompose:decompose:$decompose")
                api("com.arkivanov.decompose:extensions-compose-jetbrains:$decompose")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
                api("org.kodein.di:kodein-di:$kodein")
                implementation("org.kodein.di:kodein-di-framework-compose:$kodein")
                api("io.ktor:ktor-client-okhttp:$ktor")
                api("io.ktor:ktor-client-content-negotiation:$ktor")
                api("io.ktor:ktor-serialization-kotlinx-json:$ktor")

                implementation("com.mikepenz:aboutlibraries-compose:10.6.3")
                implementation("com.mikepenz:aboutlibraries-core:10.6.3")

                api("dev.icerock.moko:resources-compose:$moko")
                api("media.kamel:kamel-image:$kamel")
                api("io.github.aakira:napier:$napier")
                api(project(":color"))
                api(project(":game"))
                api(project(":datastore"))
                api(project(":nanoid"))
            }
        }

        val androidMain by getting {
            apply(plugin = "kotlin-parcelize")
            apply(plugin = "org.gradle.android.cache-fix")
            dependencies {
                implementation("androidx.appcompat:appcompat:1.6.1")
                implementation("androidx.core:core-ktx:1.10.1")
                implementation("androidx.activity:activity-ktx:1.7.2")
                implementation("androidx.activity:activity-compose:1.7.2")
                implementation("androidx.multidex:multidex:2.0.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines")
                implementation("com.google.android.material:material:1.9.0")
                implementation("androidx.core:core-splashscreen:1.0.1")
                implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
                implementation("androidx.compose.material3:material3-window-size-class:1.1.0")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutines")
                implementation("com.sealwu:kscript-tools:1.0.22")
                implementation("net.harawata:appdirs:1.2.1")
            }
        }
    }
}

android {
    sourceSets["main"].setRoot("src/androidMain/")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    sourceSets["main"].assets.srcDirs("src/androidMain/assets", "src/commonMain/assets")

    compileSdk = Configuration.compileSdk
    buildToolsVersion = Configuration.buildTools

    defaultConfig {
        applicationId = artifact
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
        versionCode = appCode
        versionName = appVersion

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        sourceCompatibility = CompileOptions.sourceCompatibility
        targetCompatibility = CompileOptions.targetCompatibility
    }
}

compose {
    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.4.6")

    desktop {
        application {
            mainClass = "$artifact.MainKt"

            nativeDistributions {
                packageName = "eSports Prodigy"
                packageVersion = appVersion
                description = "Get your eSports Information here."
                copyright = "© 2020 Jeff Retz (DatLag). All rights reserved."
                licenseFile.set(rootProject.file("LICENSE"))

                outputBaseDir.set(rootProject.buildDir.resolve("release"))

                when (getHost()) {
                    Host.Linux -> targetFormats(
                        TargetFormat.AppImage, TargetFormat.Deb, TargetFormat.Rpm
                    )
                    Host.MAC -> targetFormats(
                        TargetFormat.Dmg
                    )
                    Host.Windows -> targetFormats(
                        TargetFormat.Exe, TargetFormat.Msi
                    )
                }


                linux {
                    iconFile.set(file("src/commonMain/resources/MR/assets/png/launcher_128.png"))
                    rpmLicenseType = "AGPL-3.0"
                    debMaintainer = "Jeff Retz (DatLag)"
                }
                windows {
                    iconFile.set(file("src/commonMain/resources/MR/assets/ico/launcher_128.ico"))
                    upgradeUuid = "8f3be63c-60aa-4b77-a63c-dce53b962a75"
                }
                macOS {
                    iconFile.set(file("src/commonMain/resources/MR/assets/icns/launcher.icns"))
                }

                includeAllModules = true
            }
        }
    }
}

configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration>() {
    version = "1.3.0"
}
dependencies {
    implementation("io.ktor:ktor-client-okhttp-jvm:2.3.1")
}

multiplatformResources {
    multiplatformResourcesPackage = artifact
    multiplatformResourcesClassName = "SharedRes"
}

aboutLibraries {
    includePlatform = true
    duplicationMode = DuplicateMode.MERGE
    duplicationRule = DuplicateRule.GROUP
    excludeFields = arrayOf("generated")
}

fun getHost(): Host {
    return when (osdetector.os) {
        "linux" -> Host.Linux
        "osx" -> Host.MAC
        "windows" -> Host.Windows
        else -> {
            val hostOs = System.getProperty("os.name")
            val isMingwX64 = hostOs.startsWith("Windows")

            when {
                hostOs == "Linux" -> Host.Linux
                hostOs == "Mac OS X" -> Host.MAC
                isMingwX64 -> Host.Windows
                else -> throw IllegalStateException("Unknown OS: ${osdetector.classifier}")
            }
        }
    }
}

enum class Host(val label: String) {
    Linux("linux"),
    Windows("win"),
    MAC("mac");
}