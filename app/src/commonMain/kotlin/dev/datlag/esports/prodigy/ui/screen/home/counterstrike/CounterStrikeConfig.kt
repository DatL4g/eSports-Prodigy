package dev.datlag.esports.prodigy.ui.screen.home.counterstrike

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dev.datlag.esports.prodigy.model.hltv.Home

@Parcelize
sealed class CounterStrikeConfig : Parcelable {

    @Parcelize
    data class Team(val initialTeam: Home.Team) : CounterStrikeConfig(), Parcelable

    @Parcelize
    data class Article(val href: String) : CounterStrikeConfig(), Parcelable
}