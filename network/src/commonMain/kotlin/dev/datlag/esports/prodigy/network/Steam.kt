package dev.datlag.esports.prodigy.network

import com.hadiyarajesh.flower_core.ApiResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import dev.datlag.esports.prodigy.model.steam.UserStatsForGame

interface Steam {

    @GET("ISteamUserStats/GetUserStatsForGame/v0002/")
    suspend fun userStatsForGame(
        @Query("appid") gameId: String,
        @Query("key") apiKey: String,
        @Query("steamid") userId: String
    ) : ApiResponse<UserStatsForGame>
}