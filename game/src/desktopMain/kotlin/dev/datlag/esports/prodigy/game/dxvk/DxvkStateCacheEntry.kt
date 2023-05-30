package dev.datlag.esports.prodigy.game.dxvk

import dev.datlag.esports.prodigy.game.common.*
import dev.datlag.esports.prodigy.model.common.scopeCatching
import dev.datlag.esports.prodigy.model.common.suspendCatching
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.security.MessageDigest

data class DxvkStateCacheEntry(
    val header: Header?,
    val hash: ByteBuffer,
    val data: ByteBuffer
) {

    private fun dataSHA1(): ByteArray {
        val hasher = MessageDigest.getInstance("SHA-1")
        hasher.update(data.array())

        if (header == null) {
            hasher.update(DXVK.SHA1_EMPTY)
        }

        return hasher.digest()
    }

    fun isValid() = dataSHA1().contentEquals(hash.array())

    suspend fun writeTo(writer: FileChannel, edition: DxvkStateCacheEdition) = suspendCatching {
        when (edition) {
            is DxvkStateCacheEdition.Standard -> writeToStandard(writer).getOrThrow()
            is DxvkStateCacheEdition.Legacy -> writeToLegacy(writer).getOrThrow()
        }
    }

    private suspend fun writeToStandard(writer: FileChannel) = suspendCatching {
        header?.writeTo(writer)
        writer.write(ByteBuffer.wrap(hash.array()))
        writer.write(ByteBuffer.wrap(hash.array()))
    }

    private suspend fun writeToLegacy(writer: FileChannel) = suspendCatching {
        writer.write(ByteBuffer.wrap(data.array()))
        writer.write(ByteBuffer.wrap(hash.array()))
    }

    data class Header(
        val stageMask: UInt,
        val entrySize: UInt
    ) {
        suspend fun writeTo(writer: FileChannel) = suspendCatching {
            writer.writeU8(stageMask, null)
            writer.writeU24(entrySize, DXVK.ENDIAN)
        }

        companion object {
            suspend fun fromReader(reader: FileChannel): Result<Header> = suspendCatching {
                Header(
                    reader.readU8(null).getOrThrow(),
                    reader.readU24(DXVK.ENDIAN).getOrThrow()
                )
            }
        }
    }

    companion object {
        suspend fun fromReader(reader: FileChannel, header: DxvkStateCache.Header): Result<DxvkStateCacheEntry?> = suspendCatching {
            val entry = if (header.edition.isLegacy()) {
                fromReaderLegacy(reader, header.entrySize)
            } else {
                fromReaderStandard(reader)
            }.getOrThrow()
            if (!entry.isValid()) {
                null
            } else {
                entry
            }
        }

        private suspend fun fromReaderStandard(reader: FileChannel): Result<DxvkStateCacheEntry> = suspendCatching {
            val header = Header.fromReader(reader)
            val entry = withHeader(header.getOrThrow())
            val hashState = reader.read(entry.hash)
            val dataState = reader.read(entry.data)

            if (hashState == -1) {
                throw DXVKException.ExpectedEndOfFile
            } else if (dataState == -1) {
                throw DXVKException.UnexpectedEndOfFile
            }

            entry
        }

        private suspend fun fromReaderLegacy(reader: FileChannel, size: UInt): Result<DxvkStateCacheEntry> = suspendCatching {
            val entry = withLength(size)
            val dataState = reader.read(entry.data)
            val hashState = reader.read(entry.hash)

            if (dataState == -1) {
                throw DXVKException.ExpectedEndOfFile
            } else if (hashState == -1) {
                throw DXVKException.UnexpectedEndOfFile
            }

            entry
        }

        private fun withHeader(header: Header) = DxvkStateCacheEntry(
            header,
            ByteBuffer.allocate(DXVK.HASH_SIZE),
            ByteBuffer.allocate(header.entrySize.toInt())
        )

        private fun withLength(size: UInt) = DxvkStateCacheEntry(
            null,
            ByteBuffer.allocate(DXVK.HASH_SIZE),
            ByteBuffer.allocate(size.toInt() - DXVK.HASH_SIZE)
        )
    }
}
