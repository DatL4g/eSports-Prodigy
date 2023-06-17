package dev.datlag.esports.prodigy.model

import dev.datlag.esports.prodigy.nanoid.NanoIdUtils
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

    @OptIn(ExperimentalEncodingApi::class)
    val commonDecoded: String
        get() = String(Base64.decode(common))

    @OptIn(ExperimentalEncodingApi::class)
    val userDecoded: String
        get() = String(Base64.decode(user))

    companion object {
        fun generate(): UUID {
            return UUID(
                common = generateBase64(),
                user = generateBase64()
            )
        }

        @OptIn(ExperimentalEncodingApi::class)
        private fun generateBase64(): String {
            val alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~`!@#\$%^&*()_-+={[}]|\\:;\"'<,>.?/".toCharArray()
            val id = NanoIdUtils.randomNanoId(
                alphabet = alphabet
            )

            return Base64.encode(id.toByteArray())
        }
    }
}
