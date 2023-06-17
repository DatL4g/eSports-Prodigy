import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import java.util.Properties
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.security.MessageDigest
import kotlin.experimental.xor

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
val ktor = "2.3.1"
val moko = "0.23.0"
val kamel = "0.5.1"
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

                implementation("com.mikepenz:aboutlibraries-compose:10.7.0")
                implementation("com.mikepenz:aboutlibraries-core:10.7.0")

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
                implementation("com.darkrockstudios:mpfilepicker:1.1.0")
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

val createNativeLib = tasks.create<Copy>("createNativeLib") {
    dependsOn("sekret:assemble")

    val binPath = if (File("sekret/build/bin/native/releaseShared/").exists()) {
        "sekret/build/bin/native/releaseShared/"
    } else {
        "sekret/build/bin/native/debugShared/"
    }

    from(binPath)
    exclude("*.h")
    into("resources/common/")
}

fun propertiesFile(): File {
    return if (project.hasProperty("propertiesFileName")) {
        val propsPath = project.property("propertiesFileName") as? String
        if (propsPath != null) {
            File(project.rootDir, propsPath)
        } else {
            File(project.rootDir, "sekret.properties")
        }
    } else {
        File(project.rootDir, "sekret.properties")
    }
}

fun propertiesFromFile(propsFile: File = propertiesFile()): Properties {
    return Properties().apply {
        propsFile.inputStream().use {
            load(it)
        }
    }
}

fun sha256(value: String): String {
    val bytes = value.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

fun encode(value: String, packageName: String = artifact): String {
    val obfuscator = sha256(packageName)
    val obfuscatorBytes = obfuscator.encodeToByteArray()
    val obfuscatedSecretBytes = arrayListOf<Byte>()
    var i = 0

    value.toByteArray().forEach { secretByte ->
        val obfuscatorByte = obfuscatorBytes[i % obfuscatorBytes.size]
        val obfuscatedByte = secretByte.xor(obfuscatorByte)
        obfuscatedSecretBytes.add(obfuscatedByte)
        i++
    }

    var encoded = ""
    val iterator: Iterator<Byte> = obfuscatedSecretBytes.iterator()
    while (iterator.hasNext()) {
        val item = iterator.next()
        encoded += "0x" + Integer.toHexString(item.toInt() and 0xff)

        if (iterator.hasNext()) {
            encoded += ", "
        }
    }
    return encoded
}

tasks.create("createSekret") {
    doLast {
        val packageName = artifact
        val props = propertiesFromFile()
        val sekretKotlinFile = File(project.projectDir, "sekret/src/nativeMain/kotlin/sekret.kt")
        var newContent = "\n\n"

        props.entries.forEach { entry ->
            val keyName = entry.key as String
            val obfuscated = encode(entry.value as String, packageName)

            var method = "@CName(\"Java_dev_datlag_sekret_Sekret_${keyName}\")\n"
            method += "fun ${keyName}(env: CPointer<JNIEnvVar>, clazz: jclass, it: jstring): jstring {\n"
            method += "    initRuntimeIfNeeded()\n\n"
            method += "    val obfuscatedSecret = intArrayOf(\n"
            method += "        $obfuscated\n"
            method += "    )\n"
            method += "    return getOriginalKey(obfuscatedSecret, it, env) ?: it\n"
            method += "}"

            newContent += method
        }

        sekretKotlinFile.appendText(newContent)
    }
}

compose {
    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.4.6")

    desktop {
        application {
            mainClass = "$artifact.MainKt"
            dependsOn(createNativeLib)

            nativeDistributions {
                packageName = "eSports Prodigy"
                packageVersion = appVersion
                description = "Get your eSports Information here."
                copyright = "© 2020 Jeff Retz (DatLag). All rights reserved."
                licenseFile.set(rootProject.file("LICENSE"))

                outputBaseDir.set(rootProject.buildDir.resolve("release"))
                appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

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
    version = "1.4.1"
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