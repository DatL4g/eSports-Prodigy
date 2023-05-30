package dev.datlag.esports.prodigy.game.dxvk

import dev.datlag.esports.prodigy.game.common.readU32
import dev.datlag.esports.prodigy.game.common.writeU32
import dev.datlag.esports.prodigy.model.common.openReadChannel
import dev.datlag.esports.prodigy.model.common.scopeCatching
import dev.datlag.esports.prodigy.model.common.suspendCatching
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.abs
import kotlin.math.max

data class DxvkStateCache(
    val header: Header,
    val entries: List<DxvkStateCacheEntry>,
    val invalidEntries: Int
) {
    suspend fun writeTo(writer: FileChannel) = suspendCatching {
        header.writeTo(writer).getOrThrow()
        entries.forEach {
            it.writeTo(writer, header.edition).getOrThrow()
        }
    }

    data class Header(
        val magic: ByteBuffer,
        val version: UInt,
        val entrySize: UInt
    ) {
        val edition: DxvkStateCacheEdition
            get() = when {
                version > DXVK.LEGACY_VERSION.toUInt() -> DxvkStateCacheEdition.Standard
                else -> DxvkStateCacheEdition.Legacy
            }

        fun writeTo(writer: FileChannel) = scopeCatching {
            writer.write(ByteBuffer.wrap(magic.array()))
            writer.writeU32(version, DXVK.ENDIAN)
            writer.writeU32(entrySize, DXVK.ENDIAN)
        }

        companion object {
            suspend fun fromReader(reader: FileChannel): Result<Header> = suspendCatching {
                val magic = ByteBuffer.allocate(DXVK.MAGIC_BYTE_BUFFER_CAPACITY)
                reader.read(magic)

                if (String(magic.array()) != DXVK.MAGIC) {
                    throw DXVKException.ReadError(ReadErrorType.MAGIC)
                }

                val version = reader.readU32(DXVK.ENDIAN).getOrThrow()
                val entrySize = reader.readU32(DXVK.ENDIAN).getOrThrow()

                Header(
                    magic,
                    version,
                    entrySize
                )
            }
        }
    }

    companion object {
        suspend fun fromFile(file: File): Result<DxvkStateCache> = suspendCatching {
            val reader = file.openReadChannel()
            val cache = fromReader(reader).getOrThrow()
            reader.close()
            cache
        }

        suspend fun fromReader(reader: FileChannel): Result<DxvkStateCache> = suspendCatching {
            val entries: MutableList<DxvkStateCacheEntry> = mutableListOf()
            val header = Header.fromReader(reader).getOrThrow()
            var invalidEntries = 0

            if (header.version.toInt() >= DXVK.UNSUPPORTED_VERSION) {
                throw DXVKException.UnsupportedVersion
            }

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
                        entries.add(entry)
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
                invalidEntries
            )
        }
    }
}
