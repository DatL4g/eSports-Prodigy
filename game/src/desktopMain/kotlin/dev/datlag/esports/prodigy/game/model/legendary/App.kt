package dev.datlag.esports.prodigy.game.model.legendary

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class App(
    @SerialName("app_name") val appName: String? = null,
    @SerialName("art_cover") val artCover: String,
    @SerialName("folder_name") val folderName: String,
    @SerialName("is_installed") val isInstalled: Boolean,
    @SerialName("title") val title: String,
    @SerialName("install") val install: Install
) : Parcelable {

    @Parcelize
    @Serializable
    data class Install(
        @SerialName("install_path") val installPath: String? = null,
        @SerialName("is_dlc") val isDLC: Boolean
    ) : Parcelable
}
