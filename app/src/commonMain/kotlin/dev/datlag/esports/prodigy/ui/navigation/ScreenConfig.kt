package dev.datlag.esports.prodigy.ui.navigation

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
sealed class ScreenConfig : Parcelable {

    @Parcelize
    object Home : ScreenConfig(), Parcelable
}