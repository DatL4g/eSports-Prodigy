package dev.datlag.esports.prodigy.game.dxvk

import dev.datlag.esports.prodigy.game.common.readU32
import dev.datlag.esports.prodigy.game.common.writeU32
import dev.datlag.esports.prodigy.game.dxvk.entry.DxvkStateCacheEntry
import dev.datlag.esports.prodigy.model.common.*
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import kotlin.math.max

data class DxvkStateCache(
    val header: Header,
    val entries: List<DxvkStateCacheEntry>,
    val invalidEntries: Int,
    val file: File
) {

    val totalEntries = entries.size + invalidEntries

    suspend fun writeTo(writer: FileChannel) = suspendCatching {
        header.writeTo(writer).getOrThrow()
        entries.forEach {
            it.writeTo(writer).getOrThrow()
        }
    }

    suspend fun writeToFile(file: File) = suspendCatching {
        val writer = file.openWriteChannel()
        writer.use {
            writeTo(it).getOrThrow()
        }
    }

    suspend fun combine(other: DxvkStateCache) = suspendCatching {
        if (header.version != other.header.version) {
            throw DXVKException.VersionMismatch(
                header.version,
                other.header.version
            )
        }
        val newEntries = setFrom(entries, other.entries)
        DxvkStateCache(
            header = header.copy(entrySize = header.entrySize + other.header.entrySize),
            entries = newEntries.toList(),
            invalidEntries = invalidEntries + other.invalidEntries,
            file = file
        )
    }

    suspend fun repair() = suspendCatching {
        if (invalidEntries <= 0) {
            return@suspendCatching this@DxvkStateCache
        }

        val originalName = file.name
        val backupFile = file.move("$originalName.bak")

        val loadBackupFile = writeToFile(file).isFailure
        if (loadBackupFile) {
            backupFile.move(originalName)
            this@DxvkStateCache
        } else {
            fromFile(file).getOrNull() ?: this@DxvkStateCache
        }
    }

    data class Header(
        val magic: String,
        val version: UInt,
        val entrySize: UInt
    ) {

        fun writeTo(writer: FileChannel) = scopeCatching {
            writer.write(ByteBuffer.wrap(magic.toByteArray()))
            writer.writeU32(version, ByteOrder.LITTLE_ENDIAN)
            writer.writeU32(entrySize, ByteOrder.LITTLE_ENDIAN)
        }

        companion object {
            suspend fun fromReader(reader: FileChannel): Result<Header> = suspendCatching {
                val magic = ByteBuffer.allocate(4)
                reader.read(magic)

                val magicString = String(magic.array())
                if (magicString != DXVK.MAGIC) {
                    throw DXVKException.ReadError(ReadErrorType.MAGIC)
                }

                val version = reader.readU32(ByteOrder.LITTLE_ENDIAN).getOrThrow()
                val entrySize = reader.readU32(ByteOrder.LITTLE_ENDIAN).getOrThrow()

                Header(
                    magicString,
                    version,
                    entrySize
                )
            }
        }
    }

    companion object {
        suspend fun fromFile(file: File): Result<DxvkStateCache> = suspendCatching {
            val reader = file.openReadChannel()
            reader.use {
                fromReader(file, it).getOrThrow()
            }
        }

        private suspend fun fromReader(file: File, reader: FileChannel): Result<DxvkStateCache> = suspendCatching {
            val entries: MutableList<DxvkStateCacheEntry> = mutableListOf()
            val header = Header.fromReader(reader).getOrThrow()
            var invalidEntries = 0

            while (true) {
                val entryResult = suspendCatching {
                    DxvkStateCacheEntry.fromReader(reader, header).getOrThrow()
                }
                if (entryResult.isFailure) {
                    break
                } else {
                    val entry = entryResult.getOrNull()
                    if (entry == null) {
                        invalidEntries++
                    } else {
                        if (entry.isValid()) {
                            entries.add(entry)
                        } else {
                            invalidEntries++
                        }
                    }
                }
            }
            val distinctEntries = entries.distinctBy {
                it.hash.array().contentHashCode()
            }
            invalidEntries += max(entries.size - distinctEntries.size, 0)

            DxvkStateCache(
                header,
                distinctEntries,
                invalidEntries,
                file
            )
        }
    }
}
