package dev.datlag.esports.prodigy.game.common

import dev.datlag.esports.prodigy.game.dxvk.DXVK
import dev.datlag.esports.prodigy.game.dxvk.DXVKException
import dev.datlag.esports.prodigy.game.dxvk.ReadErrorType
import dev.datlag.esports.prodigy.model.common.scopeCatching
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

fun FileChannel.readU32(order: ByteOrder?): Result<UInt> = scopeCatching {
    val bytes = ByteBuffer.allocate(4)
    order?.let { bytes.order(it) }
    val state = this.read(bytes)

    if (state == -1) {
        throw DXVKException.ReadError(ReadErrorType.U32)
    }

    bytes[0].unsignedInt(order) +
            bytes[1].unsignedShl(DXVK.BYTE_POSITION_2_SHIFT, order) +
            bytes[2].unsignedShl(DXVK.BYTE_POSITION_3_SHIFT, order) +
            bytes[3].unsignedShl(DXVK.BYTE_POSITION_4_SHIFT, order)
}

fun FileChannel.readU24(order: ByteOrder?): Result<UInt> = scopeCatching {
    val bytes = ByteBuffer.allocate(3)
    order?.let { bytes.order(it) }
    val state = this.read(bytes)

    if (state == -1) {
        throw DXVKException.ReadError(ReadErrorType.U24)
    }

    bytes[0].unsignedInt(order) +
            bytes[1].unsignedShl(DXVK.BYTE_POSITION_2_SHIFT, order) +
            bytes[2].unsignedShl(DXVK.BYTE_POSITION_3_SHIFT, order)
}

fun FileChannel.readU8(order: ByteOrder?): Result<UInt> = scopeCatching {
    val bytes = ByteBuffer.allocate(1)
    order?.let { bytes.order(it) }
    val state = this.read(bytes)

    if (state == -1) {
        throw DXVKException.ReadError(ReadErrorType.U8)
    }

    bytes[0].unsignedInt(order)
}

fun FileChannel.writeU32(value: UInt, order: ByteOrder?) = scopeCatching {
    val byteArray = byteArrayOf(
        value.toByte(),
        value.shr(DXVK.BYTE_POSITION_2_SHIFT).toByte(),
        value.shr(DXVK.BYTE_POSITION_3_SHIFT).toByte(),
        value.shr(DXVK.BYTE_POSITION_4_SHIFT).toByte()
    )
    val bytes = ByteBuffer.wrap(byteArray)
    order?.let { bytes.order(it) }
    this.write(bytes)
}

fun FileChannel.writeU24(value: UInt, order: ByteOrder?) = scopeCatching {
    val byteArray = byteArrayOf(
        value.toByte(),
        value.shr(DXVK.BYTE_POSITION_2_SHIFT).toByte(),
        value.shr(DXVK.BYTE_POSITION_3_SHIFT).toByte()
    )
    val bytes = ByteBuffer.wrap(byteArray)
    order?.let { bytes.order(it) }
    this.write(bytes)
}

fun FileChannel.writeU8(value: UInt, order: ByteOrder?) = scopeCatching {
    val bytes = ByteBuffer.wrap(
        byteArrayOf(
            value.toByte()
        )
    )
    order?.let { bytes.order(it) }
    this.write(bytes)
}