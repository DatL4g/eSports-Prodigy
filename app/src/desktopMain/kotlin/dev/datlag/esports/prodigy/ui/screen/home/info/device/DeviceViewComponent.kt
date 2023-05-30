package dev.datlag.esports.prodigy.ui.screen.home.info.device

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.value.Value
import dev.datlag.esports.prodigy.common.coroutineScope
import dev.datlag.esports.prodigy.common.ioScope
import dev.datlag.esports.prodigy.game.SteamLauncher
import dev.datlag.esports.prodigy.game.model.Game
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.GameConfig
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.GameViewComponent
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import org.kodein.di.DI

actual class DeviceViewComponent actual constructor(
    componentContext: ComponentContext,
    override val di: DI
) : DeviceComponent, ComponentContext by componentContext {

    private val scope = ioScope()
    private val wantedSteamGameIds = listOf("730", "252950")
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

    override val gameManifests: Flow<List<Game>> = SteamLauncher.appManifests.transform {
        return@transform emit(it.filter { app ->
            wantedSteamGameIds.contains(app.appId)
        }.sortedBy { app -> wantedSteamGameIds.indexOf(app.appId) }.map { app ->
            SteamLauncher.asGame(app)
        })
    }

    override fun gameClicked(game: Game) {
        navigation.activate(GameConfig.Overview(game))
    }

    @Composable
    override fun render() {
        DeviceView(this@DeviceViewComponent)
    }
}