package dev.datlag.esports.prodigy.game.model.steam

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LibraryConfig(
    @SerialName("path") val path: String
) : Parcelable
