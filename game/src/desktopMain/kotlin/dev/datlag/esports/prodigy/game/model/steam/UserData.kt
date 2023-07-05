package dev.datlag.esports.prodigy.game.model.steam

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class UserData(
    @SerialName("AccountName") val accountName: String,
    @SerialName("PersonaName") val personaName: String? = accountName,
    @SerialName("MostRecent") val mostRecent: Int,
    @SerialName("Timestamp") val timestamp: Long
)

data class User(
    val id: String,
    val data: UserData,
    val avatarFile: File?
) {

    val name = data.personaName?.ifBlank { null } ?: data.accountName.ifBlank { id }

    companion object {
        fun fromMap(map: Map<String, UserData>, avatarFileResolver: (String) -> File?) = map.map { (id, data) ->
            User(id, data, avatarFileResolver(id))
        }
    }
}
