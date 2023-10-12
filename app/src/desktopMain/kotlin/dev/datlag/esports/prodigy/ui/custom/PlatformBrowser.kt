package dev.datlag.esports.prodigy.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.CEFState
import dev.datlag.esports.prodigy.LocalCEFInitialization
import dev.datlag.esports.prodigy.ui.custom.seeker.Seeker
import dev.datlag.esports.prodigy.ui.custom.seeker.SeekerDefaults
import dev.datlag.esports.prodigy.ui.custom.seeker.Segment
import dev.datlag.esports.prodigy.ui.custom.seeker.rememberSeekerState

@Composable
actual fun PlatformBrowser(content: @Composable () -> Unit) {
    val cefState by LocalCEFInitialization.current

    if (cefState == CEFState.INITIALIZED) {
        content()
    } else {
        val progress = when (val current = cefState) {
            CEFState.LOCATING -> 0F
            is CEFState.Downloading -> 25F + current.progress
            CEFState.EXTRACTING -> 125F
            CEFState.INSTALLING -> 150F
            CEFState.INITIALIZING -> 175F
            else -> return
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val seekerState = rememberSeekerState()
            val segments = listOf(
                Segment(name = "Locating", start = 0F),
                Segment(name = "Downloading", start = 25F),
                Segment(name = "Extracting", start = 125F),
                Segment(name = "Installing", start = 150F),
                Segment(name = "Initializing", start = 175F)
            )

            Text(
                text = seekerState.currentSegment.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Loading browser compatibility, please wait."
            )
            Seeker(
                modifier = Modifier.fillMaxWidth(0.5F),
                state = seekerState,
                onValueChange = { },
                value = progress,
                segments = segments,
                range = 0F..200F,
                dimensions = SeekerDefaults.seekerDimensions(
                    gap = 4.dp,
                    thumbRadius = 0.dp,
                    trackHeight = 8.dp,
                    progressHeight = 8.dp
                ),
                colors = SeekerDefaults.seekerColors()
            )
        }
    }
}