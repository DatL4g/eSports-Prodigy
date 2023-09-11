package dev.datlag.esports.prodigy.model.octane

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Events(
    @SerialName("events") val events: List<Event>
)
