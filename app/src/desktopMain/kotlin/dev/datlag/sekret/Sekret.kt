package dev.datlag.sekret

import java.security.MessageDigest
import kotlin.experimental.xor

class Sekret {

    companion object {
        init {
            NativeLoader.loadLibrary(Sekret::class.java.classLoader ?: this::class.java.classLoader, "sekret")
        }

        private fun sha256(value: String): String {
            val bytes = value.encodeToByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }

        fun encode(value: String, key: String): ByteArray {
            val obfuscator = sha256(key)
            val obfuscatorBytes = obfuscator.encodeToByteArray()
            val obfuscatedSecretBytes = value.encodeToByteArray().mapIndexed { index, secretByte ->
                secretByte.xor(obfuscatorBytes[index % obfuscatorBytes.size])
            }
            return obfuscatedSecretBytes.toByteArray()
        }

        fun decode(value: ByteArray, key: String): String {
            val obfuscator = sha256(key)
            val obfuscatorBytes = obfuscator.encodeToByteArray()

            val out = value.mapIndexed { index, byte ->
                byte.xor(obfuscatorBytes[index % obfuscatorBytes.size])
            }.toByteArray()

            return String(out)
        }
    }

    external fun userCipher(it: String): String?
}