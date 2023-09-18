import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import org.gradle.jvm.tasks.Jar
import java.util.Properties
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.security.MessageDigest
import kotlin.experimental.xor

plugins {
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.android.application)
    alias(libs.plugins.cache.fix) apply false
    alias(libs.plugins.compose)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.moko.resources)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.osdetector)
    id("kotlin-parcelize") apply false
    alias(libs.plugins.serialization)
    alias(libs.plugins.sekret)
}

val artifact = "dev.datlag.esports.prodigy"
val appVersion = "1.0.0"
val appCode = 100

group = artifact
version = appVersion

val javafx = CompileOptions.jvmTarget
val javafxModules = listOf(
    "javafx.base",
    "javafx.graphics", // depends on base
    "javafx.controls", // depends on base & graphics
    "javafx.media", // depends on base & graphics
    "javafx.swing", // depends on base & graphics
    "javafx.web", // depends on base & graphics & controls & media
)

kotlin {
    androidTarget("android") {
        jvmToolchain(CompileOptions.jvmTargetVersion)
    }
    jvm("desktop") {
        jvmToolchain(CompileOptions.jvmTargetVersion)
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.material3)
                api(compose.materialIconsExtended)
                api(compose.ui)
                api(compose.animation)
                api(compose.animationGraphics)
                api(compose.uiTooling)
                api(libs.compose.ui.util)

                api(libs.stdlib)

                api(libs.decompose)
                api(libs.decompose.compose)
                api(libs.coroutines)
                api(libs.kodein)
                api(libs.kodein.compose)

                implementation(libs.aboutlibraries)
                implementation(libs.aboutlibraries.compose)

                api(libs.moko.resources.compose)
                api(libs.kamel)
                api(libs.napier)
                api(libs.filepicker)
                api(libs.webview)
                api(libs.reveal)
                api(libs.reveal.shapes)

                api(libs.windowsize.multiplatform)
                api(libs.insetsx)

                api(project(":color"))
                api(project(":game"))
                api(project(":datastore"))
                api(project(":nanoid"))
                api(project(":charts"))
                api(project(":network"))
                api(project(":database"))
                api(project(":compose-collapsing-toolbar"))
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)

            apply(plugin = "kotlin-parcelize")
            apply(plugin = "org.gradle.android.cache-fix")
            dependencies {
                implementation(libs.activity)
                implementation(libs.activity.compose)
                implementation(libs.android)
                implementation(libs.appcompat)
                implementation(libs.coroutines.android)
                implementation(libs.ktor.okhttp)
                implementation(libs.lifecycle)
                implementation(libs.material)
                implementation(libs.multidex)
                implementation(libs.sekret)
                implementation(libs.splashscreen)
            }
        }

        val desktopMain by getting {
            dependsOn(commonMain)

            dependencies {
                implementation(compose.desktop.currentOs)

                implementation(libs.appdirs)
                implementation(libs.batik.transcoder)
                implementation(libs.context.menu)
                implementation(libs.coroutines.swing)
                implementation(libs.kscript)
                implementation(libs.ktor.okhttp)
                implementation(libs.sekret)

                implementation(libs.jna)
                implementation(libs.jna.platform)
                implementation(libs.jfa.get().toString()) {
                    exclude(group = "net.java.dev.jna", module = "jna")
                }

                implementation(libs.window.styler)

                val javaFxSuffix = getJavaFxSuffix()
                javafxModules.forEach { artifact ->
                    implementation(javaFxLib(artifact, javafx, javaFxSuffix))
                }

                implementation(project(":terminal"))
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
    namespace = artifact

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

    packaging {
        resources.merges.add("META-INF/LICENSE")
        resources.merges.add("META-INF/DEPENDENCIES")
        resources.pickFirsts.add("**")
        resources.pickFirsts.add("*")
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
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
    val bytes = value.encodeToByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

fun encode(value: String, packageName: String = artifact): String {
    val obfuscator = sha256(packageName)
    val obfuscatorBytes = obfuscator.encodeToByteArray()
    val obfuscatedSecretBytes = arrayListOf<Byte>()
    var i = 0

    value.encodeToByteArray().forEach { secretByte ->
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

val createSekretFile = tasks.create("createSekretFile") {
    doLast {
        val sekretKotlinFile = File(project.projectDir, "sekret/src/nativeMain/kotlin/sekret.kt")

        sekretKotlinFile.writeText(buildString {
            appendLine("package dev.datlag.sekret")
            appendLine()
            appendLine("import dev.datlag.sekret.JNIEnvVar")
            appendLine("import dev.datlag.sekret.jclass")
            appendLine("import dev.datlag.sekret.jstring")
            appendLine("import kotlinx.cinterop.*")
            appendLine()
            appendLine()
        })
    }
}

val createSekret = tasks.create("createSekret") {
    dependsOn(createSekretFile)
    doLast {
        val packageName = artifact
        val props = propertiesFromFile()
        val sekretKotlinFile = File(project.projectDir, "sekret/src/nativeMain/kotlin/sekret.kt")
        var newContent = "\n\n"

        props.entries.forEach { entry ->
            val keyName = entry.key as String
            val obfuscated = encode(entry.value as String, packageName)

            var method = "@CName(\"Java_dev_datlag_sekret_Sekret_${keyName}\")\n"
            method += "fun ${keyName}(env: CPointer<JNIEnvVar>, clazz: jclass, it: jstring): jstring? {\n"
            method += "    initRuntimeIfNeeded()\n\n"
            method += "    val obfuscatedSecret = intArrayOf(\n"
            method += "        $obfuscated\n"
            method += "    )\n"
            method += "    return getOriginalKey(obfuscatedSecret, it, env)\n"
            method += "}"

            newContent += method
        }

        sekretKotlinFile.appendText(newContent)
    }
}

val createNativeLib = tasks.create<Copy>("createNativeLib") {
    dependsOn(createSekret, "sekret:assemble")

    val binPath = if (File("sekret/build/bin/native/releaseShared/").exists()) {
        "sekret/build/bin/native/releaseShared/"
    } else {
        "sekret/build/bin/native/debugShared/"
    }

    from(binPath)
    exclude("*.h")
    into("resources/common/")
}

compose {
    desktop {
        application {
            mainClass = "$artifact.MainKt"
            dependsOn(createNativeLib)

            nativeDistributions {
                packageName = "eSports-Prodigy"
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
                    iconFile.set(file("src/commonMain/resources/MR/assets/png/launcher_1024.png"))
                    rpmLicenseType = "AGPL-3.0"
                    debMaintainer = "Jeff Retz (DatLag)"
                }
                windows {
                    iconFile.set(file("src/commonMain/resources/MR/assets/ico/launcher_192.ico"))
                    upgradeUuid = "8f3be63c-60aa-4b77-a63c-dce53b962a75"
                }
                macOS {
                    iconFile.set(file("src/commonMain/resources/MR/assets/icns/launcher.icns"))
                }

                includeAllModules = true
                javafxModules.forEach(modules::add)
            }
        }
    }
}

configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
    version = libs.versions.ktorfit.asProvider().get()
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

fun getJavaFxSuffix(): String {
    return when (osdetector.classifier) {
        "linux-x86_64" -> "linux"
        "linux-aarch_64" -> "linux-aarch64"
        "windows-x86_64" -> "win"
        "osx-x86_64" -> "mac"
        "osx-aarch_64" -> "mac-aarch64"
        else -> getHost().label
    }
}

fun javaFxLib(artifactId: String, version: String, suffix: String): String {
    return "org.openjfx:${artifactId.replace('.', '-')}:${version}:${suffix}"
}

enum class Host(val label: String) {
    Linux("linux"),
    Windows("win"),
    MAC("mac");
}
