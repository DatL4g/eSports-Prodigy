package dev.datlag.esports.prodigy.game.dxvk.entry

import dev.datlag.esports.prodigy.game.common.readU24
import dev.datlag.esports.prodigy.game.common.readU8
import dev.datlag.esports.prodigy.game.common.writeU24
import dev.datlag.esports.prodigy.game.common.writeU8
import dev.datlag.esports.prodigy.game.dxvk.DXVK
import dev.datlag.esports.prodigy.game.dxvk.DXVKException
import dev.datlag.esports.prodigy.model.common.suspendCatching
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

data class V8(
    val header: Header,
    override val hash: ByteBuffer,
    override val data: ByteBuffer
) : DxvkStateCacheEntry(
    data = data,
    hash = hash
) {

    override suspend fun writeTo(writer: FileChannel) = suspendCatching {
        header.writeTo(writer)
        writer.write(ByteBuffer.wrap(hash.array()))
        writer.write(ByteBuffer.wrap(data.array()))
    }

    data class Header(
        val stageMask: UInt,
        val entrySize: UInt
    ) {

        suspend fun writeTo(writer: FileChannel) = suspendCatching {
            writer.writeU8(stageMask, null)
            writer.writeU24(entrySize, ByteOrder.LITTLE_ENDIAN)
        }

        companion object {
            suspend fun fromReader(reader: FileChannel): Result<Header> = suspendCatching {
                Header(
                    reader.readU8(null).getOrThrow(),
                    reader.readU24(ByteOrder.LITTLE_ENDIAN).getOrThrow()
                )
            }
        }
    }

    companion object {
        suspend fun fromReader(reader: FileChannel): Result<V8> = suspendCatching {
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

        private fun withHeader(header: Header) = V8(
            header,
            ByteBuffer.allocate(DXVK.HASH_SIZE),
            ByteBuffer.allocate(header.entrySize.toInt())
        )
    }
}