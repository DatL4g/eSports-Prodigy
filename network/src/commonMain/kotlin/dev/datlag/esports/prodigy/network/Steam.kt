package dev.datlag.esports.prodigy.network

import com.hadiyarajesh.flower_core.ApiResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Path
import dev.datlag.esports.prodigy.model.steam.UserProfile

interface Steam {

    @Headers(
        "Accept: text/xml"
    )
    @GET("profiles/{id}/?xml=1")
    suspend fun userProfile(@Path("id") id: String) : ApiResponse<UserProfile>
}