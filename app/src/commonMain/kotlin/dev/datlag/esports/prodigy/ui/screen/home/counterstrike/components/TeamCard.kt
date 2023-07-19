@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.ui.isDesktop
import dev.datlag.esports.prodigy.ui.theme.LocalDarkMode
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource
import io.ktor.http.*

@Composable
fun TeamCard(team: Home.Team) {
    ElevatedCard {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = team.ranking.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            TeamIcon(team)
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = team.name
            )
            Spacer(modifier = Modifier.weight(1F))
            OutlinedButton(
                onClick = {

                }
            ) {
                Text(text = "Details")
            }
        }
    }
}

@Composable
private fun TeamIcon(team: Home.Team) {
    val (preferredIcon, fallbackIcon) = if (LocalDarkMode.current) {
        team.imgDark to team.imgLight
    } else {
        team.imgLight to team.imgDark
    }

    when (val resource = asyncPainterResource(preferredIcon)) {
        is Resource.Loading -> {
            Spacer(modifier = Modifier.size(24.dp))
        }
        is Resource.Success -> {
            // apply tint to SVGs as they don't support css (yet)
            val isSVG = remember { false }// isSVG(preferredIcon) && isDesktop }
            Image(
                modifier = Modifier.size(24.dp),
                painter = resource.value,
                contentDescription = team.name,
                contentScale = ContentScale.FillBounds,
                colorFilter = if (isSVG) ColorFilter.tint(LocalContentColor.current) else null
            )
        }
        is Resource.Failure -> {
            when (val fallbackResource = asyncPainterResource(fallbackIcon)) {
                is Resource.Loading -> {
                    Spacer(modifier = Modifier.size(24.dp))
                }
                is Resource.Success -> {
                    val isSVG = remember { isSVG(fallbackIcon) && isDesktop }
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = fallbackResource.value,
                        contentDescription = team.name,
                        contentScale = ContentScale.FillBounds,
                        colorFilter = if (isSVG) ColorFilter.tint(LocalContentColor.current) else null
                    )
                }
                is Resource.Failure -> {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = team.name
                    )
                }
            }
        }
    }
}

fun <I : Any> isSVG(data: I): Boolean {
    val dataPath = when (data) {
        is Url -> data
        else -> runCatching {
            Url(data.toString())
        }.getOrNull()
    }?.encodedPath

    return (dataPath ?: data.toString()).substringAfterLast('.').equals("svg", true)
}