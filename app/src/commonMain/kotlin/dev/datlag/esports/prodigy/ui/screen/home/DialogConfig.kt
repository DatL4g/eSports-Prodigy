package dev.datlag.esports.prodigy.ui.screen.home

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
sealed class DialogConfig : Parcelable {

    @Parcelize
    data object AnalyzeDXVK : DialogConfig(), Parcelable
}