package dev.datlag.esports.prodigy.game.dxvk

sealed class ReadErrorType {
    object MAGIC : ReadErrorType()
    object U32 : ReadErrorType()
    object U24 : ReadErrorType()
    object U8 : ReadErrorType()
}