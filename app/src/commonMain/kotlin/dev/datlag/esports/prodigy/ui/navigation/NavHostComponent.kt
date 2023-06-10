package dev.datlag.esports.prodigy.ui.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import dev.datlag.esports.prodigy.ui.screen.home.HomeScreenComponent
import dev.datlag.esports.prodigy.ui.screen.user.UserScreenComponent
import org.kodein.di.DI

class NavHostComponent private constructor(
    componentContext: ComponentContext,
    override val di: DI
) : Component, ComponentContext by componentContext {

    private val navigation = StackNavigation<ScreenConfig>()
    private val stack = childStack(
        source = navigation,
        initialConfiguration = ScreenConfig.Home,
        childFactory = ::createScreenComponent
    )

    private fun createScreenComponent(
        screenConfig: ScreenConfig,
        componentContext: ComponentContext
    ) : Component {
        return when (screenConfig) {
            is ScreenConfig.Home -> HomeScreenComponent(
                componentContext,
                di,
                ::goToUser
            )
            is ScreenConfig.User -> UserScreenComponent(
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
            animation = stackAnimation(fade())
        ) {
            it.instance.render()
        }
    }

    private fun goBack() {
        navigation.pop()
    }

    private fun goToUser() {
        navigation.push(ScreenConfig.User)
    }

    companion object {
        fun create(componentContext: ComponentContext, di: DI): NavHostComponent {
            return NavHostComponent(componentContext, di)
        }
    }
}