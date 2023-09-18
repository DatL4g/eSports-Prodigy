package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.article

import dev.datlag.esports.prodigy.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface ArticleComponent : Component {

    val href: String

    fun back()
}