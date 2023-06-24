package dev.datlag.esports.prodigy.common

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.esports.prodigy.ui.WindowSize
import dev.icerock.moko.resources.FontResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
actual fun <T> Flow<T>.collectAsStateSafe(initial: () -> T): State<T> = this.collectAsStateWithLifecycle(initial())

@Composable
actual fun <T> StateFlow<T>.collectAsStateSafe(): State<T> = this.collectAsStateWithLifecycle()

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun WindowSize.Companion.basedOnWidth(activity: Activity): WindowSize {
    return when (calculateWindowSizeClass(activity).widthSizeClass) {
        WindowWidthSizeClass.Compact -> WindowSize.COMPACT
        WindowWidthSizeClass.Medium -> WindowSize.MEDIUM
        else -> WindowSize.EXPANDED
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun WindowSize.Companion.basedOnHeight(activity: Activity): WindowSize {
    return when (calculateWindowSizeClass(activity).heightSizeClass) {
        WindowHeightSizeClass.Compact -> WindowSize.COMPACT
        WindowHeightSizeClass.Medium -> WindowSize.MEDIUM
        else -> WindowSize.EXPANDED
    }
}

actual fun FontResource.toComposeFont(
    weight: FontWeight,
    style: FontStyle
): Font {
    return Font(
        resId = this.fontResourceId,
        weight = weight,
        style = style
    )
}

@OptIn(ExperimentalFoundationApi::class)
actual fun Modifier.onClick(
    enabled: Boolean,
    onDoubleClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    onClick: () -> Unit
): Modifier {
    return this.combinedClickable(
        enabled = enabled,
        onDoubleClick = onDoubleClick,
        onLongClick = onLongClick,
        onClick = onClick
    )
}