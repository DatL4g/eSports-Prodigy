package dev.datlag.sekret

import dev.datlag.esports.prodigy.model.common.systemProperty
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.nio.file.Files

object NativeLoader {

    @Suppress("UnsafeDynamicallyLoadedCode")
    fun loadLibrary(classLoader: ClassLoader, libName: String) {
        try {
            val resDir = systemProperty("compose.application.resources.dir")?.let { File(it) }
            val libFile = File(resDir, libFilename(libName))

            System.load(libFile.canonicalPath)
        } catch (ignored: Throwable) {
            try {
                System.loadLibrary(libName)
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
        if (SystemUtils.IS_OS_WINDOWS) {
            return "$libName.dll"
        } else if (SystemUtils.IS_OS_MAC) {
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