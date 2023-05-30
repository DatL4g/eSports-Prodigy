package dev.datlag.esports.prodigy.ui.screen.home

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
sealed class View : Parcelable {

    @Parcelize
    object Info : View(), Parcelable

    @Parcelize
    object CounterStrike : View(), Parcelable
}