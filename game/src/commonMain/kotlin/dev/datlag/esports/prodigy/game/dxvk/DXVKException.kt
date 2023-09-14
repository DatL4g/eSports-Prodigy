package dev.datlag.esports.prodigy.game.dxvk

sealed class DXVKException : Exception() {
    object UnsupportedVersion : DXVKException() {
        override val message: String
            get() = "This version of DXVK is not supported"
    }

    object ExpectedEndOfFile : DXVKException() {
        override val message: String
            get() = "Expected end of file, this is probably not an error"
    }

    object UnexpectedEndOfFile : DXVKException() {
        override val message: String
            get() = "Unexpected end of file, could not read (whole) DXVK cache"
    }

    object InvalidEntry : DXVKException() {
        override val message: String
            get() = "Got an invalid entry, this may occur on mismatching hash"
    }

    data class VersionMismatch(val current: UInt, val other: UInt) : DXVKException() {

        override val message: String
            get() = "Can't combine the provided caches as their version does not equal. Current: $current, Other: $other"
    }

    data class ReadError(val type: ReadErrorType) : DXVKException() {

        private val errorMessageType: String = when (type) {
            ReadErrorType.MAGIC -> "magic is wrong"
            ReadErrorType.U8 -> "u8"
            ReadErrorType.U24 -> "u24"
            ReadErrorType.U32 -> "u32"
        }

        override val message: String
            get() = "Got a read error, $errorMessageType"
    }
}