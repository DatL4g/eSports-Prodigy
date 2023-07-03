package dev.datlag.esports.prodigy.game.model.steam

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    @SerialName("AccountName") val accountName: String,
    @SerialName("PersonaName") val personaName: String? = accountName,
    @SerialName("MostRecent") val mostRecent: Int,
    @SerialName("Timestamp") val timestamp: Long
)

data class User(
    val id: String,
    val data: UserData
) {
    companion object {
        fun fromMap(map: Map<String, UserData>) = map.map { (id, data) ->
            User(id, data)
        }
    }
}
