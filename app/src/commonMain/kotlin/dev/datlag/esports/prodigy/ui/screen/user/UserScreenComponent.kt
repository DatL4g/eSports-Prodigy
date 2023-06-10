package dev.datlag.esports.prodigy.ui.screen.user

import com.arkivanov.decompose.ComponentContext
import org.kodein.di.DI

expect class UserScreenComponent(
    componentContext: ComponentContext,
    di: DI,
    back: () -> Unit
) : UserComponent