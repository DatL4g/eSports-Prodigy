package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.news

import dev.datlag.esports.prodigy.model.state.cs.NewsRequest
import dev.datlag.esports.prodigy.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface NewsComponent : Component {

    val newsState: Flow<NewsRequest>

    fun back()
}