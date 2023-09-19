@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.common.*
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.ui.isDesktop
import dev.datlag.esports.prodigy.ui.loadImageScheme
import dev.datlag.esports.prodigy.ui.theme.LocalDarkMode
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource

@Composable
fun TeamCard(
    team: Home.Team,
    index: Int,
    onDetailsClicked: () -> Unit
) {
    var cardSize by remember { mutableStateOf(32) }

    ElevatedCard(modifier = Modifier.onSizeChanged {
        cardSize = it.height
    }.onClick {
        onDetailsClicked()
    }.bounceClick(0.95F)) {
        Row(
            modifier = Modifier.fillMaxWidth().ifTrue(index <= 2) { shimmer(
                shimmerColor = when (index) {
                    0 -> Color(0xFFFFD700)
                    1 -> Color(0xFFC0C0C0)
                    else -> Color(0xFFBF8970)
                }
            ) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamIcon(cardSize, team)
            TeamRanking(team.ranking)
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = team.name
            )
            Spacer(modifier = Modifier.weight(1F))
            IconButton(
                onClick = {
                    onDetailsClicked()
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun TeamRanking(ranking: Int) {
    val style = when (ranking) {
        1 -> MaterialTheme.typography.titleLarge
        2 -> MaterialTheme.typography.titleMedium
        3 -> MaterialTheme.typography.titleSmall
        else -> MaterialTheme.typography.bodyMedium
    }

    Text(
        modifier = Modifier.padding(start = 16.dp, end = 8.dp),
        text = ranking.toString(),
        fontWeight = FontWeight.Bold,
        style = style
    )
}

@Composable
private fun TeamIcon(size: Int, team: Home.Team) {
    val (preferredIcon, fallbackIcon) = if (LocalDarkMode.current) {
        team.imgDark to team.imgLight
    } else {
        team.imgLight to team.imgDark
    }
    val (suffix, fallbackSuffix) = if (LocalDarkMode.current) {
        "dark" to "light"
    } else {
        "light" to "dark"
    }
    val themeKey = "${team.id}-$suffix"

    SchemeTheme(themeKey) {
        Surface(
            modifier = Modifier.size(size.dp),
            shape = CardDefaults.elevatedShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            val background = if (LocalDarkMode.current) {
                Color.Black
            } else {
                Color.White
            }
            Box(
                modifier = Modifier.fillMaxSize().background(background.copy(alpha = 0.7F)),
                contentAlignment = Alignment.Center
            ) {
                when (val resource = asyncPainterResource(preferredIcon)) {
                    is Resource.Loading -> {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                    is Resource.Success -> {
                        loadImageScheme(themeKey, resource.value)
                        Image(
                            modifier = Modifier.size(48.dp),
                            painter = resource.value,
                            contentDescription = team.name,
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    is Resource.Failure -> {
                        when (val fallbackResource = asyncPainterResource(fallbackIcon)) {
                            is Resource.Loading -> {
                                Spacer(modifier = Modifier.size(48.dp))
                            }
                            is Resource.Success -> {
                                loadImageScheme("${team.id}-$fallbackSuffix", fallbackResource.value)
                                Image(
                                    modifier = Modifier.size(48.dp),
                                    painter = fallbackResource.value,
                                    contentDescription = team.name,
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            is Resource.Failure -> {
                                Icon(
                                    modifier = Modifier.size(48.dp),
                                    imageVector = Icons.Default.BrokenImage,
                                    contentDescription = team.name
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
