package dev.datlag.esports.prodigy.network

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Query
import dev.datlag.esports.prodigy.model.octane.Events

interface OctaneAPI {

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("events")
    suspend fun events(
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 10
    ): Events
}