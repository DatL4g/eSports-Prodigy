package dev.datlag.esports.prodigy.game.dxvk.entry

import dev.datlag.esports.prodigy.game.dxvk.DXVK
import dev.datlag.esports.prodigy.game.dxvk.DXVKException
import dev.datlag.esports.prodigy.model.common.suspendCatching
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

data class V16(
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
        val entryType: UInt,
        val stageMask: UInt,
        val entrySize: UInt
    ) {

        suspend fun writeTo(writer: FileChannel) = suspendCatching {
            val byteArray = ByteArray(4)

            if (entryType.toInt() != 0) {
                byteArray[0] = (byteArray[0].toInt() or 0x01).toByte()
            } else {
                byteArray[0] = (byteArray[0].toInt() and 0xFE).toByte()
            }

            byteArray[0] = (byteArray[0].toInt() or (stageMask.toInt() shl 1)).toByte()
            byteArray[0] = (byteArray[0].toInt() or (entrySize.toInt() and 0x03) shl 6).toByte()
            byteArray[1] = (entrySize.toInt() shr 2 and 0xFF).toByte()
            byteArray[2] = (entrySize.toInt() shr 10 and 0xFF).toByte()
            byteArray[3] = (entrySize.toInt() shr 18 and 0xFF).toByte()

            writer.write(ByteBuffer.wrap(byteArray))
        }

        companion object {
            suspend fun fromReader(reader: FileChannel): Result<Header> = suspendCatching {
                val full32Bit = ByteBuffer.allocate(4)
                val full32BitState = reader.read(full32Bit)

                if (full32BitState == -1) {
                    throw DXVKException.ExpectedEndOfFile
                }

                val byteArray = full32Bit.array()
                val entryType = (byteArray[3].toInt() and 0x01)
                val stageMask = ((byteArray[0].toInt() shr 1) and 0x1F)
                val entrySize = ((byteArray[0].toInt() ushr 6) and 0x03) or
                        ((byteArray[1].toInt() and 0xFF) shl 2) or
                        ((byteArray[2].toInt() and 0xFF) shl 10) or
                        ((byteArray[3].toInt() and 0xFF) shl 18)

                Header(
                    entryType = entryType.toUInt(),
                    stageMask = stageMask.toUInt(),
                    entrySize = entrySize.toUInt()
                )
            }
        }
    }

    companion object {
        suspend fun fromReader(reader: FileChannel): Result<V16> = suspendCatching {
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

        private fun withHeader(header: Header) = V16(
            header,
            ByteBuffer.allocate(DXVK.HASH_SIZE),
            ByteBuffer.allocate(header.entrySize.toInt())
        )
    }
}
