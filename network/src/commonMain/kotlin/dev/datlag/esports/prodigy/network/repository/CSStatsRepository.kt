package dev.datlag.esports.prodigy.network.repository

import com.hadiyarajesh.flower_core.Resource
import com.hadiyarajesh.flower_core.dbBoundResource
import dev.datlag.esports.prodigy.model.steam.UserStatsForGame
import dev.datlag.esports.prodigy.network.Steam
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transform

class CSStatsRepository(
    val steamAPI: Steam
) {

    val userStats: MutableStateFlow<UserStatsForGame?> = MutableStateFlow(null)

    fun getUserStats(apiKey: String, userId: String) = dbBoundResource(
        makeNetworkRequest = {
            steamAPI.userStatsForGame("730", apiKey, userId)
        },
        fetchFromLocal = {
            userStats
        },
        shouldMakeNetworkRequest = {
            it == null
        },
        saveResponseData = {
            userStats.emit(it)
        }
    )

    fun userStats(apiKey: String, userId: String) = getUserStats(apiKey, userId).transform {
        return@transform emit(when (it.status) {
            is Resource.Status.Error -> {
                val status = (it.status as? Resource.Status.Error)
                println(status?.message)
                status?.data ?: userStats.value
            }
            is Resource.Status.Loading -> (it.status as? Resource.Status.Loading)?.data ?: userStats.value
            is Resource.Status.Success -> (it.status as? Resource.Status.Success)?.data ?: userStats.value
            is Resource.Status.EmptySuccess -> userStats.value
        })
    }

}