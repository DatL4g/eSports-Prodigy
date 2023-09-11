package dev.datlag.esports.prodigy.ui.theme

import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import com.dzirbel.contextmenu.MaterialContextMenuRepresentation
import dev.datlag.esports.prodigy.ui.ContextMenuColors

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun SchemeThemeSystemProvider(scheme: ColorScheme, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalContextMenuRepresentation provides MaterialContextMenuRepresentation(colors = ContextMenuColors(MaterialTheme.colorScheme))
    ) {
        content()
    }
}