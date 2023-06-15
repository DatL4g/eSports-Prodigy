plugins {
    kotlin("multiplatform")
    id("com.google.osdetector")
}

group = "dev.datlag"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    val nativeTarget = when (getHost()) {
        Host.Linux -> linuxX64("native")
        Host.MAC -> macosX64("native")
        Host.Windows -> mingwX64("native")
    }

    nativeTarget.apply {
        binaries {
            sharedLib()
        }
        compilations["main"].cinterops.create("sekret") {
            val javaHome = System.getenv("JAVA_HOME") ?: System.getProperty("java.home")
            packageName = "dev.datlag.sekret"

            includeDirs(
                Callable { File(javaHome, "include") },
                Callable { File(javaHome, "include/darwin") },
                Callable { File(javaHome, "include/linux") },
                Callable { File(javaHome, "include/win32") }
            )
        }
    }
    sourceSets {
        val nativeMain by getting
    }
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
