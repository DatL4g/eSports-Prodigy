package dev.datlag.esports.prodigy.ui.screen.home.info.device.game

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dev.datlag.esports.prodigy.game.model.LocalGame

@Parcelize
sealed class GameConfig : Parcelable {

    @Parcelize
    data class Overview(val game: LocalGame) : GameConfig(), Parcelable
}