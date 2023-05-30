package dev.datlag.esports.prodigy.game.model.steam

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class AppManifest(
    @SerialName("appid") val appId: String,
    @SerialName("name") val name : String,
    @SerialName("installdir") val installDir: String
) : Parcelable
