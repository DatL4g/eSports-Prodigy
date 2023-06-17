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
    @Secret @SerialName("common") val common: List<Int>,
    @Secret @SerialName("user") val user: List<Int>
) {

    @Secret
    val commonDecoded: String = decode(common)

    @Secret
    val userDecoded: String = decode(user)

    private fun decode(value: List<Int>): String {
        val key = Sekret().userCipher(
            getPackageName()
        ) ?: throw IllegalStateException("Could not load native encryption")
        return Sekret.decode(value, key)
    }

    companion object {
        fun generate(): UUID {
            return UUID(
                common = generateUniqueId(),
                user = generateUniqueId()
            )
        }

        private fun generateUniqueId(): List<Int> {
            val alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~`!@#\$%^&*()_-+={[}]|\\:;\"'<,>.?/".toCharArray()
            val id = NanoIdUtils.randomNanoId(
                alphabet = alphabet
            )
            val key = Sekret().userCipher(
                getPackageName()
            ) ?: throw IllegalStateException("Could not load native encryption")

            return Sekret.encode(id, key)
        }
    }
}
