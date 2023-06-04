package dev.datlag.esports.prodigy.ui.screen.home.info.device.game.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import dev.datlag.esports.prodigy.game.model.Game
import dev.datlag.esports.prodigy.ui.screen.home.info.device.FallbackImage
import dev.datlag.esports.prodigy.ui.screen.home.info.device.LoadingImage
import io.kamel.core.Resource
import io.kamel.image.lazyPainterResource

@Composable
fun BoxScope.GameHero(game: Game) {
    when (val resource = lazyPainterResource(game.heroUrl ?: run {
        when (game) {
            is Game.Steam -> game.heroFile
            is Game.Multi -> game.heroFile
            else -> null
        }
    } ?: String())) {
        is Resource.Loading -> {
            LoadingImage(game.name, resource.progress)
        }
        is Resource.Success -> {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = resource.value,
                contentDescription = game.name,
                contentScale = ContentScale.FillWidth
            )
        }
        is Resource.Failure -> {
            val fallbackFile = when (game) {
                is Game.Steam -> game.heroFile
                is Game.Multi -> game.heroFile
                else -> null
            }

            if (fallbackFile != null) {
                when (val fallbackResource = lazyPainterResource(fallbackFile)) {
                    is Resource.Loading -> {
                        LoadingImage(game.name, fallbackResource.progress)
                    }
                    is Resource.Success -> {
                        Image(
                            modifier = Modifier.fillMaxWidth(),
                            painter = fallbackResource.value,
                            contentDescription = game.name,
                            contentScale = ContentScale.FillWidth
                        )
                    }
                    is Resource.Failure -> {
                        FallbackImage(game.name)
                    }
                }
            } else {
                FallbackImage(game.name)
            }
        }
    }
}