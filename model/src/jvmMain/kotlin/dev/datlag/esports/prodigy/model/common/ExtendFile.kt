package dev.datlag.esports.prodigy.model.common

import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.stream.Collectors

fun File.openReadChannel(): FileChannel {
    val reader = RandomAccessFile(this, "r")
    return reader.channel
}

fun File.openWriteChannel(): FileChannel {
    val writer = RandomAccessFile(this, "rw")
    return writer.channel
}

fun File?.existsSafely(): Boolean {
    if (this == null) {
        return false
    }

    return scopeCatching {
        Files.exists(this.toPath())
    }.getOrNull() ?: scopeCatching {
        this.exists()
    }.getOrNull() ?: false
}

fun File.canReadSafely(): Boolean {
    return scopeCatching {
        Files.isReadable(this.toPath())
    }.getOrNull() ?: scopeCatching {
        this.canRead()
    }.getOrNull() ?: false
}

fun File.canWriteSafely(): Boolean {
    return scopeCatching {
        Files.isWritable(this.toPath())
    }.getOrNull() ?: scopeCatching {
        this.canWrite()
    }.getOrNull() ?: false
}

fun File?.existsRSafely(): Boolean {
    if (this == null) {
        return false
    }

    return existsSafely() && canReadSafely()
}

fun File?.existsRWSafely(): Boolean {
    if (this == null) {
        return false
    }

    return existsSafely() && canReadSafely() && canWriteSafely()
}

fun File.isSymlinkSafely(): Boolean {
    return scopeCatching {
        Files.isSymbolicLink(this.toPath())
    }.getOrNull() ?: scopeCatching {
        !Files.isRegularFile(this.toPath(), LinkOption.NOFOLLOW_LINKS)
    }.getOrNull() ?: false
}

fun File.getRealFile(): File {
    return if (isSymlinkSafely()) scopeCatching {
        Files.readSymbolicLink(this.toPath()).toFile()
    }.getOrNull() ?: this else this
}

fun File.isSame(file: File?): Boolean {
    return if (file == null) {
        false
    } else {
        this == file || scopeCatching {
            this.absoluteFile == file.absoluteFile || Files.isSameFile(this.toPath(), file.toPath())
        }.getOrNull() ?: false
    }
}

fun Collection<File>.normalize(): List<File> {
    val list: MutableList<File> = mutableListOf()
    this.forEach { file ->
        var realFile = file.getRealFile()
        if (!realFile.existsSafely()) {
            if (file.existsSafely()) {
                realFile = file
            } else {
                return@forEach
            }
        }
        if (list.firstOrNull { it.isSame(realFile) } == null) {
            list.add(realFile)
        }
    }
    return list
}

fun File.listFilesSafely(): List<File> {
    return scopeCatching {
        this.listFiles()
    }.getOrNull()?.filterNotNull() ?: scopeCatching {
        Files.list(this.toPath()).collect(Collectors.toList()).mapNotNull { path ->
            path?.toFile()
        }
    }.getOrNull() ?: emptyList()
}

fun File.mkdirsSafely(): Boolean = scopeCatching {
    this.mkdirs()
}.getOrNull() ?: false

fun File.deleteSafely(): Boolean {
    return scopeCatching {
        Files.delete(this.toPath())
    }.isSuccess || scopeCatching {
        this.delete()
    }.getOrNull() ?: false
}

fun File.move(name: String): File {
    return scopeCatching {
        Files.move(this.toPath(), File(this.parent, name).toPath())
    }.getOrNull()?.toFile() ?: scopeCatching {
        val targetFile = File(this.parent, name)
        if (this.renameTo(targetFile)) {
            targetFile
        } else {
            null
        }
    }.getOrNull() ?: this
}

fun findSystemRoots(): List<File> {
    val windowsRoot = systemEnv("SystemDrive")
    val roots = (scopeCatching {
        FileSystems.getDefault()?.rootDirectories?.mapNotNull {
            it?.toFile()
        }
    }.getOrNull() ?: scopeCatching {
        File.listRoots().filterNotNull()
    }.getOrNull()?.toList()?.ifEmpty { null } ?: emptyList()).normalize()

    return (if (!windowsRoot.isNullOrBlank()) {
        roots.sortedByDescending {
            it.absolutePath.trim().equals(windowsRoot, true) || it.isSame(File(windowsRoot))
        }
    } else {
        roots
    }).also {
        println(it.map { f -> f.absolutePath })
    }
}

fun File.isDirectorySafely(): Boolean {
    return scopeCatching {
        this.isDirectory
    }.getOrNull() ?: scopeCatching {
        Files.isDirectory(this.toPath())
    }.getOrNull() ?: false
}