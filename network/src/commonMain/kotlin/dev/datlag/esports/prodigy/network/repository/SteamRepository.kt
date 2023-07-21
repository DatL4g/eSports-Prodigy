package dev.datlag.esports.prodigy.network.repository

import com.hadiyarajesh.flower_core.Resource
import com.hadiyarajesh.flower_core.dbBoundResource
import com.hadiyarajesh.flower_core.networkResource
import dev.datlag.esports.prodigy.model.steam.UserProfile
import dev.datlag.esports.prodigy.network.Steam
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform

class SteamRepository(
    private val steam: Steam
) {
    private val userProfiles: MutableMap<String, UserProfile> = mutableMapOf()

    fun userProfile(id: String) = dbBoundResource(
        makeNetworkRequest = {
            steam.userProfile(id)
        },
        shouldMakeNetworkRequest = {
            it == null
        },
        fetchFromLocal = {
            flowOf(userProfiles.getOrDefault(id, null))
        },
        saveResponseData = {
            userProfiles[id] = it
        }
    ).transform {
        return@transform emit(when (it.status) {
            is Resource.Status.Loading -> {
                (it.status as? Resource.Status.Loading)?.data
            }
            is Resource.Status.EmptySuccess -> {
                userProfiles.getOrDefault(id, null)
            }
            is Resource.Status.Error -> {
                (it.status as? Resource.Status.Error)?.data
            }
            is Resource.Status.Success -> {
                (it.status as? Resource.Status.Success)?.data
            }
        })
    }
}