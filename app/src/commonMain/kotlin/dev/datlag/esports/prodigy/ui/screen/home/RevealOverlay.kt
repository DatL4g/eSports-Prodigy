package dev.datlag.esports.prodigy.ui.screen.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.svenjacobs.reveal.Key
import com.svenjacobs.reveal.RevealOverlayArrangement
import com.svenjacobs.reveal.RevealOverlayScope
import com.svenjacobs.reveal.shapes.balloon.Arrow
import com.svenjacobs.reveal.shapes.balloon.Balloon

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun RevealOverlayScope.RevealOverlay(key: Key) {
    val revealTop = when (calculateWindowSizeClass().widthSizeClass) {
        WindowWidthSizeClass.Compact -> true
        else -> false
    }

    when (key) {
        RevealKeys.Navigation -> {
            if (revealTop) {
                OverlayText(
                    modifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                    label = "Navigation",
                    arrow = Arrow.bottom()
                )
            } else {
                OverlayText(
                    modifier = Modifier.align(
                        horizontalArrangement = RevealOverlayArrangement.End
                    ),
                    label = "Navigation",
                    arrow = Arrow.start()
                )
            }
        }
        RevealKeys.Features -> OverlayText(
            modifier = Modifier.align(
                horizontalArrangement = RevealOverlayArrangement.Start
            ),
            label = "Additional Features",
            arrow = Arrow.end()
        )
    }
}

@Composable
private fun OverlayText(label: String, arrow: Arrow, modifier: Modifier = Modifier) {
    Balloon(
        modifier = modifier.padding(8.dp),
        arrow = arrow,
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        elevation = 2.dp,
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = label,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
        )
    }
}