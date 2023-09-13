package dev.datlag.esports.prodigy.game.common

import java.nio.ByteOrder

fun Byte.unsignedInt(order: ByteOrder?): UInt {
    val byte = this.toUByte()
    return if (order == ByteOrder.LITTLE_ENDIAN) {
        byte.toUShort().toUInt()
    } else {
        byte.toUInt()
    }
}

fun UInt.unsignedByte(order: ByteOrder?): UByte {
    return if (order == ByteOrder.LITTLE_ENDIAN) {
        this.toUShort().toUByte()
    } else {
        this.toUByte()
    }
}

fun Byte.unsignedShl(bitCount: Int, order: ByteOrder?): UInt {
    return unsignedInt(order).shl(bitCount)
}

fun UInt.unsignedShr(bitCount: Int, order: ByteOrder?): UByte {
    return this.shr(bitCount).unsignedByte(order)
}