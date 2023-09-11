package dev.datlag.esports.prodigy.ui.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.*
import com.arkivanov.decompose.router.stack.*
import dev.datlag.esports.prodigy.ui.screen.home.HomeScreenComponent
import dev.datlag.esports.prodigy.ui.screen.user.UserScreenComponent
import dev.datlag.esports.prodigy.ui.screen.settings.SettingsScreenComponent
import dev.datlag.esports.prodigy.ui.screen.welcome.WelcomeScreenComponent
import org.kodein.di.DI
import org.kodein.di.instance
import dev.datlag.esports.prodigy.datastore.preferences.AppSettings
import kotlinx.coroutines.flow.map
import dev.datlag.esports.prodigy.common.getValueBlocking

class NavHostComponent private constructor(
    componentContext: ComponentContext,
    override val di: DI
) : Component, ComponentContext by componentContext {

    private val navigation = StackNavigation<ScreenConfig>()
    private val appSettings: DataStore<AppSettings> by di.instance()

    private val stack = childStack(
        source = navigation,
        initialConfiguration = run {
            val welcomed = appSettings.data.map { it.welcomed }.getValueBlocking(false)
            if (welcomed) {
                ScreenConfig.Home
            } else {
                ScreenConfig.Welcome
            }
        },
        childFactory = ::createScreenComponent
    )

    private fun createScreenComponent(
        screenConfig: ScreenConfig,
        componentContext: ComponentContext
    ) : Component {
        return when (screenConfig) {
            is ScreenConfig.Welcome -> WelcomeScreenComponent(
                componentContext,
                di
            ) { goToHome(replace = true) }
            is ScreenConfig.Home -> HomeScreenComponent(
                componentContext,
                di,
                ::goToUser,
                ::goToSettings
            )
            is ScreenConfig.User -> UserScreenComponent(
                componentContext,
                di,
                ::goBack
            )
            is ScreenConfig.Settings -> SettingsScreenComponent(
                componentContext,
                di,
                ::goBack
            )
        }
    }

    @Composable
    override fun render() {
        Children(
            stack = stack,
            animation = rootScreenAnimation()
        ) {
            it.instance.render()
        }
    }

    fun rootScreenAnimation(
        animationSpec: FiniteAnimationSpec<Float> = tween()
    ): StackAnimation<ScreenConfig, Component> = stackAnimation { child ->
        when (child.configuration) {
            is ScreenConfig.Settings, is ScreenConfig.User -> {
                slideVertically(
                    SlideDirection.FromTopToBottom,
                    animationSpec
                )
            }
            else -> fade()
        }
    }

    fun slideVertically(
        slideDirection: SlideDirection,
        animationSpec: FiniteAnimationSpec<Float> = tween(),
    ): StackAnimator = stackAnimator(animationSpec) { factor, _, content ->
        content(slideDirection.offset(factor, LocalLayoutDirection.current))
    }

    private fun goToHome(replace: Boolean) {
        if (replace) {
            navigation.replaceCurrent(ScreenConfig.Home)
        } else {
            navigation.push(ScreenConfig.Home)
        }
    }

    private fun goBack() {
        navigation.pop()
    }

    private fun goToUser() {
        navigation.push(ScreenConfig.User)
    }

    private fun goToSettings(offset: Offset?) {
        navigation.push(ScreenConfig.Settings) {
            // settingsOffset = null
        }
    }

    companion object {
        fun create(componentContext: ComponentContext, di: DI): NavHostComponent {
            return NavHostComponent(componentContext, di)
        }
    }
}