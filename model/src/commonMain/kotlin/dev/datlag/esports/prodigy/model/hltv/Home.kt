package dev.datlag.esports.prodigy.model.hltv

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dev.datlag.esports.prodigy.model.common.getDigitsOrNull
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Home(
    val hero: Hero?,
    val teams: List<Home.Team>,
    val news: List<Home.News>
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
    ) : Parcelable {

        val id: Number
            get() {
                val regex = "(team)?(\\/)?(\\d+)\\/.*".toRegex(RegexOption.IGNORE_CASE)
                val result = regex.find(href)

                return result?.groupValues?.getOrNull(3)?.toIntOrNull()
                    ?: result?.value?.getDigitsOrNull()?.toIntOrNull()
                    ?: href.getDigitsOrNull()?.toIntOrNull() ?: 0
            }
    }

    @Serializable
    @Parcelize
    data class News(
        val title: String,
        val href: String
    ) : Parcelable
}