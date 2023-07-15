package dev.datlag.esports.prodigy.ui.screen.settings

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
sealed class DialogConfig : Parcelable {
    @Parcelize
    object SteamFinder : DialogConfig(), Parcelable
}