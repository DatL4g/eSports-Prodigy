package dev.datlag.esports.prodigy.game.dxvk.entry

import dev.datlag.esports.prodigy.game.dxvk.DXVK
import dev.datlag.esports.prodigy.game.dxvk.DXVKException
import dev.datlag.esports.prodigy.model.common.suspendCatching
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.security.MessageDigest

data class Legacy(
    override val data: ByteBuffer,
    override val hash: ByteBuffer
) : DxvkStateCacheEntry(
    data = data,
    hash = hash
) {

    override fun dataSHA1(): ByteArray {
        val hasher = MessageDigest.getInstance("SHA-1")
        hasher.update(data.array())

        return hasher.digest()
    }

    override suspend fun writeTo(writer: FileChannel) = suspendCatching {
        writer.write(ByteBuffer.wrap(data.array()))
        writer.write(ByteBuffer.wrap(hash.array()))
    }

    companion object {
        suspend fun fromReader(reader: FileChannel, entrySize: UInt): Result<Legacy> = suspendCatching {
            val entry = withLength(entrySize)
            val dataState = reader.read(entry.data)
            val hashState = reader.read(entry.hash)

            if (dataState == -1) {
                throw DXVKException.ExpectedEndOfFile
            } else if (hashState == -1) {
                throw DXVKException.UnexpectedEndOfFile
            }

            entry
        }

        private fun withLength(size: UInt) = Legacy(
            hash = ByteBuffer.allocate(DXVK.HASH_SIZE),
            data = ByteBuffer.allocate(size.toInt() - DXVK.HASH_SIZE)
        )
    }
}