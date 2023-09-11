package dev.datlag.esports.prodigy.model.octane

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Event(
    @SerialName("_id") val id: String,
    @SerialName("slug") val slug: String = id,
    @SerialName("name") val name: String,
    @SerialName("region") val region: String,
    @SerialName("mode") val mode: Int,
    @SerialName("image") val image: String?
) : Parcelable
