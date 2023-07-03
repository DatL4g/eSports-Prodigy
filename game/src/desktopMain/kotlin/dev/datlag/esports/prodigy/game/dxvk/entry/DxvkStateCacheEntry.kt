package dev.datlag.esports.prodigy.game.dxvk.entry

import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache
import dev.datlag.esports.prodigy.model.common.suspendCatching
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.security.MessageDigest

sealed class DxvkStateCacheEntry(
    open val data: ByteBuffer,
    open val hash: ByteBuffer
) {

    protected open fun dataSHA1(): ByteArray {
        val hasher = MessageDigest.getInstance("SHA-1")
        hasher.update(data.array())

        return hasher.digest()
    }

    protected open fun dataSHA256(): ByteArray {
        val hasher = MessageDigest.getInstance("SHA-256")
        hasher.update(data.array())

        return hasher.digest()
    }

    fun isValid() = dataSHA1().contentEquals(hash.array()) || dataSHA256().contentEquals(hash.array())

    abstract suspend fun writeTo(writer: FileChannel): Result<Int>

    companion object {
        suspend fun fromReader(reader: FileChannel, header: DxvkStateCache.Header): Result<DxvkStateCacheEntry> = suspendCatching {
            val version = header.version.toInt()

            when {
                version >= 16 -> V16.fromReader(reader).getOrThrow()
                version >= 8 -> V8.fromReader(reader).getOrThrow()
                else -> Legacy.fromReader(reader, header.entrySize).getOrThrow()
            }
        }
    }
}
