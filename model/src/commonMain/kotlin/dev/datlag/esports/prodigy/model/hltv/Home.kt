package dev.datlag.esports.prodigy.model.hltv

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Home(
    val hero: Hero?,
    val teams: List<Home.Team>
) : Parcelable {

    @Parcelize
    @Serializable
    data class Hero(
        val img: String,
        val href: String
    ) : Parcelable

    @Serializable
    @Parcelize
    data class Team(
        val ranking: Int,
        val imgLight: String,
        val imgDark: String,
        val name: String,
        val href: String
    ) : Parcelable
}