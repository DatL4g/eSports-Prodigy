package dev.datlag.esports.prodigy.ui.screen.home

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
sealed class View : Parcelable {

    @Parcelize
    data object Info : View(), Parcelable

    @Parcelize
    data object CounterStrike : View(), Parcelable

    @Parcelize
    data object RocketLeague : View(), Parcelable

    @Parcelize
    data object Other : View(), Parcelable
}