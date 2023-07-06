package dev.datlag.esports.prodigy.game.model.steam

import dev.datlag.esports.prodigy.game.ValveDataFormat
import dev.datlag.esports.prodigy.model.common.scopeCatching
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import java.io.File

@Serializable
data class UserData(
    @SerialName("AccountName") val accountName: String,
    @SerialName("PersonaName") val personaName: String? = accountName,
    @SerialName("MostRecent") val mostRecent: Int,
    @SerialName("Timestamp") val timestamp: Long
)

@Serializable
data class LocalConfig(
    @SerialName("friends") private val _friends: Map<String, JsonElement>
) {

    @Transient
    val friends: List<Friend> = run {
        _friends.mapNotNull { (key, data) ->
            scopeCatching {
                ValveDataFormat.json.decodeFromJsonElement<FriendData>(data)
            }.getOrNull()?.let { key to it }
        }.map { (key, data) ->
            Friend(key, data)
        }
    }

    @Serializable
    data class FriendData(
        @SerialName("avatar") val avatar: String,
        @SerialName("name") val name: String
    )
}

data class Friend(
    val id: String,
    val data: LocalConfig.FriendData
) {

    companion object {
        fun fromMap(map: Map<String, LocalConfig.FriendData>) = map.map { (id, data) ->
            Friend(id, data)
        }
    }
}

data class User(
    val id: String,
    val data: UserData,
    val avatarFile: File?
) {

    val name = data.personaName?.ifBlank { null } ?: data.accountName.ifBlank { id }

    companion object {
        inline fun fromMap(map: Map<String, UserData>, avatarFileResolver: (String) -> File?) = map.map { (id, data) ->
            User(id, data, avatarFileResolver(id))
        }
    }
}
