package dev.datlag.esports.prodigy.model.steam

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class UserStatsForGame(
    @SerialName("playerstats") val playerStats: PlayerStats
) {

    @Serializable
    data class PlayerStats(
        @SerialName("stats") val stats: List<Statistic> = emptyList(),
        @SerialName("achievements") val achievements: List<Achievement> = emptyList()
    ) {

        @Serializable
        data class Statistic(
            @SerialName("name") val name: String,
            @SerialName("value") val value: Int
        )

        @Serializable
        data class Achievement(
            @SerialName("name") val name: String,
            @SerialName("achieved") val achieved: Int
        )
    }
}
