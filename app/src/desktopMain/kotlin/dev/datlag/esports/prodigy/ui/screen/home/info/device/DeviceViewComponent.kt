package dev.datlag.esports.prodigy.ui.screen.home.info.device

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.value.Value
import dev.datlag.esports.prodigy.color.createTheme
import dev.datlag.esports.prodigy.color.utils.StringUtils
import dev.datlag.esports.prodigy.common.coroutineScope
import dev.datlag.esports.prodigy.common.ioScope
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.game.HeroicLauncher
import dev.datlag.esports.prodigy.game.SteamLauncher
import dev.datlag.esports.prodigy.game.model.Game
import dev.datlag.esports.prodigy.model.common.listFrom
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.GameConfig
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.GameViewComponent
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import org.kodein.di.DI
import java.awt.Image

actual class DeviceViewComponent actual constructor(
    componentContext: ComponentContext,
    override val di: DI
) : DeviceComponent, ComponentContext by componentContext {

    private val scope = ioScope()
    private val wantedSteamGameIds = listOf("730", "252950")
    private val wantedHeroicGames = listOf("Rocket League", "Rocket League®")

    private val navigation = OverlayNavigation<GameConfig>()
    private val _child = childOverlay(
        source = navigation,
        initialConfiguration = {
            SchemeTheme.resetColorScheme(ioScope())
            GameConfig.EMPTY
        },
        handleBackButton = true
    ) { config, componentContext ->
        when (config) {
            is GameConfig.Overview -> GameViewComponent(
                componentContext,
                config.game,
                di
            ) {
                navigation.activate(GameConfig.EMPTY) {
                    SchemeTheme.resetColorScheme(ioScope())
                }
            }

            else -> config
        }
    }
    override val child: Value<ChildOverlay<GameConfig, Any>> = _child

    private val steamGames: Flow<List<Game>> = SteamLauncher.appManifests.transform {
        return@transform emit(it.filter { app ->
            wantedSteamGameIds.contains(app.appId)
        }.sortedBy { app -> wantedSteamGameIds.indexOf(app.appId) }.map { app ->
            SteamLauncher.asGame(app)
        })
    }

    private val heroicGames = HeroicLauncher.apps.transform {
        return@transform emit(it.filter { app ->
            wantedHeroicGames.contains(app.title) && app.isInstalled && !app.install.isDLC
        }.map { app ->
            HeroicLauncher.asGame(app)
        })
    }

    override val games: Flow<List<Game>> = combine(steamGames, heroicGames) { steam, heroic ->
        Game.flatten(listFrom(steam, heroic))
    }

    override fun gameClicked(game: Game) {
        navigation.activate(GameConfig.Overview(game))
    }

    override fun loadSchemeFor(gameTitle: String, image: Image) {
        if (!SchemeTheme.themes.contains(gameTitle)) {
            scope.launchIO {
                SchemeTheme.themes[gameTitle] = image.createTheme()
            }
        }
    }

    @Composable
    override fun render() {
        DeviceView(this@DeviceViewComponent)
    }
}