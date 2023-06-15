package dev.datlag.sekret

import dev.datlag.esports.prodigy.model.common.systemProperty
import java.io.File
import java.nio.file.Files

object NativeLoader {

    @Suppress("UnsafeDynamicallyLoadedCode")
    fun loadLibrary(classLoader: ClassLoader, libName: String) {
        try {
            System.loadLibrary(libName)
        } catch (ignored: Throwable) {
            try {
                val resDir = File(systemProperty("compose.application.resources.dir"))
                val libFile = File(resDir, libFilename(libName))

                System.load(libFile.canonicalPath)
            } catch (ignored: Throwable) {
                val url = classLoader.getResource(libFilename(libName))

                try {
                    val file = Files.createTempFile("jni", libFilename(nameOnly(libName))).toFile()
                    file.deleteOnExit()
                    file.delete()

                    url?.openStream()?.use {
                        Files.copy(it, file.toPath())
                    }
                    System.load(file.canonicalPath)
                } catch (e: Throwable) {
                    throw e
                }
            }
        }
    }

    private fun libFilename(libName: String): String {
        val osName = System.getProperty("os.name").lowercase()
        if (osName.indexOf("win") >= 0) {
            return "$libName.dll"
        } else if (osName.indexOf("mac") >= 0) {
            return decorateLibraryName(libName, ".dylib")
        }
        return decorateLibraryName(libName, ".so")
    }

    private fun nameOnly(libName: String): String {
        val pos = libName.lastIndexOf('/')
        if (pos >= 0) {
            return libName.substring(pos + 1)
        }
        return libName
    }

    private fun decorateLibraryName(libraryName: String, suffix: String): String {
        if (libraryName.endsWith(suffix)) {
            return libraryName
        }
        val pos = libraryName.lastIndexOf('/')
        if (pos >= 0) {
            return libraryName.substring(0, pos + 1) + "lib" + libraryName.substring(pos + 1) + suffix
        }
        return "lib$libraryName$suffix"
    }
}