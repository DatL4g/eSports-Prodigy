package dev.datlag.esports.prodigy.model

import dev.datlag.esports.prodigy.getPackageName
import dev.datlag.esports.prodigy.nanoid.NanoIdUtils
import dev.datlag.sekret.Sekret
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.afanasev.sekret.Secret
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class UUID(
    @Secret @SerialName("common") val common: String,
    @Secret @SerialName("user") val user: String
) {

    @Secret
    val commonDecoded: String = decode(common)

    @Secret
    val userDecoded: String = decode(user)

    @OptIn(ExperimentalEncodingApi::class)
    private fun decode(value: String): String {
        val key = Sekret().userCipher(
            getPackageName()
        ) ?: throw IllegalStateException("Could not load native encryption")
        return Sekret.decode(Base64.decode(value.toByteArray()), key)
    }

    companion object {
        fun generate(): UUID {
            return UUID(
                common = generateUniqueId(),
                user = generateUniqueId()
            )
        }

        @OptIn(ExperimentalEncodingApi::class)
        private fun generateUniqueId(): String {
            val alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~`!@#\$%^&*()_-+={[}]|\\:;\"'<,>.?/".toCharArray()
            val id = NanoIdUtils.randomNanoId(
                alphabet = alphabet
            )
            val key = Sekret().userCipher(
                getPackageName()
            ) ?: throw IllegalStateException("Could not load native encryption")

            return Base64.encode(Sekret.encode(id, key))
        }
    }
}
