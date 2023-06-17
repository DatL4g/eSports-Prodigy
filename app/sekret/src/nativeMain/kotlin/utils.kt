package dev.datlag.sekret

import dev.datlag.sekret.JNIEnvVar
import dev.datlag.sekret.jstring
import kotlinx.cinterop.*
import org.komputing.khash.sha256.Sha256
import kotlin.experimental.xor

fun getOriginalKey(
    secret: IntArray,
    obfuscatingString: jstring,
    env: CPointer<JNIEnvVar>
): jstring? {
    val obfuscator = obfuscatingString.getString(env)
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

fun CPointer<JNIEnvVar>.newString(chars: CPointer<jcharVar>, length: Int): jstring? {
    val method = pointed.pointed?.NewString ?: error("Could not find NewString method in JNI")
    return method.invoke(this, chars, length)
}

fun String.toJString(env: CPointer<JNIEnvVar>): jstring? {
    return memScoped {
        env.newString(wcstr.ptr, length)
    }
}

fun jstring.getStringUTFChars(env: CPointer<JNIEnvVar>): CPointer<ByteVar>? {
    val method = env.pointed.pointed?.GetStringUTFChars ?: error("Could not find GetStringUTFChars")
    return method.invoke(env, this, null)
}

fun jstring.getString(env: CPointer<JNIEnvVar>): String {
    val chars = getStringUTFChars(env)
    return chars?.toKStringFromUtf8() ?: error("Unable to create a String from the given jstring")
}