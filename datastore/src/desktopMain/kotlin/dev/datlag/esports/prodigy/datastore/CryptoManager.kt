@file:Suppress("NewApi")

package dev.datlag.esports.prodigy.datastore

import dev.datlag.esports.prodigy.model.common.existsRWSafely
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

actual class CryptoManager(
    val alias: String,
    val secretKeyStore: File,
    val commonId: String,
    val id: String
) {

    private val keyStore: KeyStore = loadOrCreateKeyStore()

    private val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE, getKey())
    }

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    actual fun encrypt(bytes: ByteArray, outputStream: OutputStream): ByteArray {
        val encryptedBytes = encryptCipher.doFinal(bytes)
        outputStream.use {
            it.write(encryptCipher.iv.size)
            it.write(encryptCipher.iv)
            it.write(encryptedBytes)
        }
        return encryptedBytes
    }

    actual fun decrypt(inputStream: InputStream): ByteArray {
        return inputStream.use {
            val iv = ByteArray(it.read())
            it.read(iv)

            val encryptedBytes = it.readBytes()

            getDecryptCipherForIv(iv).doFinal(encryptedBytes)
        }
    }

    private fun loadOrCreateKeyStore(): KeyStore {
        return if (secretKeyStore.existsRWSafely()) {
            KeyStore.getInstance(secretKeyStore, commonId.toCharArray()).apply {
                load(FileInputStream(secretKeyStore), commonId.toCharArray())
            }
        } else {
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, null)
            keyStore.store(FileOutputStream(secretKeyStore), commonId.toCharArray())
            keyStore
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(alias, KeyStore.PasswordProtection(id.toCharArray())) as? KeyStore.SecretKeyEntry?
        return existingKey?.secretKey ?: createKey().also {
            keyStore.setEntry(
                alias,
                KeyStore.SecretKeyEntry(it),
                KeyStore.PasswordProtection(id.toCharArray())
            )
            keyStore.store(FileOutputStream(secretKeyStore), commonId.toCharArray())
        }
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).generateKey()
    }

    companion object {
        private const val ALGORITHM = "AES"
        private const val BLOCK_MODE = "CBC"
        private const val PADDING = "PKCS5Padding"

        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

}