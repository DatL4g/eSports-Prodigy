package dev.datlag.esports.prodigy.ui.screen.settings

import com.arkivanov.decompose.ComponentContext
import org.kodein.di.DI

expect class SettingsScreenComponent(
    componentContext: ComponentContext,
    di: DI,
    back: () -> Unit
) : SettingsComponent