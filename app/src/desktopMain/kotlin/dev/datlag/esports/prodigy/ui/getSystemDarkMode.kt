package dev.datlag.esports.prodigy.ui

import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.LightDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.LayoutDirection
import com.mayakapps.compose.windowstyler.WindowBackdrop
import com.mayakapps.compose.windowstyler.WindowCornerPreference
import com.mayakapps.compose.windowstyler.WindowFrameStyle
import com.mayakapps.compose.windowstyler.WindowStyleManager
import dev.datlag.esports.prodigy.color.createTheme
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.model.common.scopeCatching
import dev.datlag.esports.prodigy.other.Constants
import dev.datlag.esports.prodigy.ui.theme.*
import evalBash
import org.apache.commons.lang3.SystemUtils
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.awt.Window
import java.util.Comparator
import kotlin.math.abs
import kotlin.math.max

val LocalWindow = compositionLocalOf<ComposeWindow> { error("No window state provided") }

@Composable
actual fun getSystemDarkMode(initValue: Boolean): MutableState<Boolean> {
    val detector = remember { ThemeDetector.create() }
    val isDark = remember { mutableStateOf(initValue || (currentSystemTheme == SystemTheme.DARK) || detector.isDark) }

    detector.listen { newDark ->
        isDark.value = newDark
    }

    return isDark
}

@Composable
actual fun loadImageScheme(key: Any, painter: Painter) {
    if (!SchemeTheme.containsScheme(key)) {
        val awtImage = painter.toAwtImage(
            LocalDensity.current,
            LayoutDirection.Ltr
        )

        SchemeTheme.createColorScheme(key) {
            awtImage.createTheme()
        }
    }
}

actual val isDesktop: Boolean = true

@Composable
actual fun SystemProvider(content: @Composable () -> Unit) {
    val isDarkTheme = LocalDarkMode.current
    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground
    val backdrop = WindowBackdrop.Solid(backgroundColor)

    val contextMenuStyling = if (isDarkTheme) {
        DarkDefaultContextMenuRepresentation
    } else {
        LightDefaultContextMenuRepresentation
    }

    WindowStyle(
        backdropType = backdrop,
        frameStyle = WindowFrameStyle(
            borderColor = backgroundColor,
            titleBarColor = backgroundColor,
            captionColor = onBackgroundColor,
            cornerPreference = WindowCornerPreference.ROUNDED
        )
    )

    CompositionLocalProvider(
        LocalContextMenuRepresentation provides contextMenuStyling,
        LocalScaling provides windowScaling()
    ) {
        content()
    }
}

@Composable
private fun WindowStyle(
    window: ComposeWindow = LocalWindow.current,
    isDarkTheme: Boolean = LocalDarkMode.current,
    backdropType: WindowBackdrop = WindowBackdrop.Default,
    frameStyle: WindowFrameStyle = WindowFrameStyle()
) {
    val manager = remember(isDarkTheme) { WindowStyleManager(
        window = window,
        isDarkTheme = isDarkTheme,
        backdropType = backdropType,
        frameStyle = frameStyle
    ) }

    LaunchedEffect(isDarkTheme) {
        manager.isDarkTheme = isDarkTheme
    }

    LaunchedEffect(backdropType) {
        manager.backdropType = backdropType
    }
}

@Composable
private fun windowScaling(
    window: Window = LocalWindow.current
): Double {
    val device = windowDevice(window)

    val initScale = scopeCatching {
        if (SystemUtils.IS_OS_LINUX) {
            GtkUtilities.scaleFactor()
        } else {
            device.defaultConfiguration.defaultTransform.scaleX
        }
    }.getOrNull() ?: 0.0

    return if (initScale <= 0.0) {
        scopeCatching {
            device.displayMode.width / device.defaultConfiguration.bounds.width
        }.getOrNull()?.toDouble() ?: 1.0
    } else {
        initScale
    }
}

fun windowDevice(window: Window): GraphicsDevice {
    fun square(rect: Rectangle): Int {
        return abs(rect.width * rect.height)
    }

    val bounds = window.bounds
    val device = scopeCatching {
        GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices.filter { d ->
            d.defaultConfiguration.bounds.intersects(bounds)
        }.maxByOrNull { d ->
            square(d.defaultConfiguration.bounds.intersection(bounds))
        }
    }.getOrNull() ?: window.graphicsConfiguration.device

    return device
}