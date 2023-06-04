package dev.datlag.esports.prodigy.game.model.legendary

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class AppLibrary(
    @SerialName("library") val library: List<App>
) : Parcelable
