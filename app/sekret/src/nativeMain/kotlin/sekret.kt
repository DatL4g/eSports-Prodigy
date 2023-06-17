package dev.datlag.sekret

import dev.datlag.sekret.JNIEnvVar
import dev.datlag.sekret.jclass
import dev.datlag.sekret.jstring
import kotlinx.cinterop.*
import org.komputing.khash.sha256.Sha256
import kotlin.experimental.xor

fun getOriginalKey(
    secret: IntArray,
    obfuscatingString: jstring,
    env: CPointer<JNIEnvVar>
): jstring? {
    val obfuscator = env.getString(obfuscatingString)
    val obfuscatorBytes = Sha256.digest(obfuscator.encodeToByteArray())
    val hex = obfuscatorBytes.fold("") { str, it ->
        val value = it.toUByte().toString(16)
        str + if (value.length == 1) {
            "0$value"
        } else {
            value
        }
    }
    val hexBytes = hex.encodeToByteArray()

    val out = secret.mapIndexed { index, it ->
        val obfuscatorByte = hexBytes[index % hexBytes.size]
        it.toByte().xor(obfuscatorByte)
    }.toByteArray()

    return out.toKString().toJString(env)
}