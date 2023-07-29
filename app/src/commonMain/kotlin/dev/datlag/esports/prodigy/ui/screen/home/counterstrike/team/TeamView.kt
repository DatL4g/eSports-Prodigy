package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.team

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.common.Tooltip
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.common.scaled
import dev.datlag.esports.prodigy.common.tilt
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.model.hltv.Team
import dev.datlag.esports.prodigy.ui.loadImageScheme
import dev.datlag.esports.prodigy.ui.theme.CountryImage
import dev.datlag.esports.prodigy.ui.theme.LocalDarkMode
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import dev.icerock.moko.resources.ImageResource
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.ui.LocalCommonizer

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun TeamView(component: TeamComponent) {
    val team by component.team.collectAsStateSafe { null }
    val teamId = remember(team) { team?.id ?: component.initialTeam.id }
    val suffix = if (LocalDarkMode.current) {
        "dark"
    } else {
        "light"
    }
    val (columnPadding, extraPadding) = when (calculateWindowSizeClass().widthSizeClass) {
        WindowWidthSizeClass.Medium -> PaddingValues(16.dp) to PaddingValues(0.dp)
        WindowWidthSizeClass.Expanded -> PaddingValues(16.dp) to PaddingValues(0.dp)
        else -> PaddingValues(0.dp) to PaddingValues(horizontal = 16.dp)
    }

    SchemeTheme("$teamId-$suffix") {
        LazyColumn(
            contentPadding = columnPadding
        ) {
            item {
                Row(
                    modifier = Modifier.fillParentMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            component.back()
                        },
                        modifier = Modifier.background(
                            color = Color.Black.copy(alpha = 0.5F),
                            shape = CircleShape
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    TeamIcon(team, component.initialTeam, teamId)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (team != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    modifier = Modifier.size(18.dp),
                                    painter = painterResource(CountryImage.getByCode(team!!.country.code)),
                                    contentDescription = team!!.country.name,
                                    contentScale = ContentScale.Inside
                                )
                                Text(
                                    text = team!!.country.name
                                )
                            }
                        }
                        Text(
                            text = team?.name ?: component.initialTeam.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    FlowRow(
                        maxItemsInEachRow = 2,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val commonizer = LocalCommonizer.current

                        team?.socials?.let { socials ->
                            socials.instagram?.let {
                                FilledIconButton(
                                    onClick = {
                                        commonizer.openInBrowser(
                                            url = it
                                        )
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        painter = painterResource(SharedRes.images.Instagram),
                                        contentDescription = "Instagram",
                                    )
                                }
                            }
                            socials.twitter?.let {
                                FilledIconButton(
                                    onClick = {
                                        commonizer.openInBrowser(
                                            url = it
                                        )
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        painter = painterResource(SharedRes.images.Twitter),
                                        contentDescription = "Twitter"
                                    )
                                }
                            }
                            socials.facebook?.let {
                                FilledIconButton(
                                    onClick = {
                                        commonizer.openInBrowser(
                                            url = it
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Facebook,
                                        contentDescription = "Facebook"
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.fillParentMaxWidth().padding(extraPadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Players",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    FlowRow(
                        modifier = Modifier.fillParentMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        team?.players?.filter { it.type.isPlayer }?.forEach { player ->
                            PlayerCard(player)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalFoundationApi::class)
@Composable
private fun PlayerCard(player: Team.Player) {
    ElevatedCard(
        modifier = Modifier.padding(vertical = 4.dp).tilt(10F, true)
    ) {
        Box(
            modifier = Modifier.defaultMinSize(minWidth = 50.dp, minHeight = 50.dp)
        ) {
            val maxWidth = when (calculateWindowSizeClass().widthSizeClass) {
                WindowWidthSizeClass.Expanded -> 200.dp.scaled(100.dp)
                WindowWidthSizeClass.Medium -> 120.dp.scaled(60.dp)
                else -> 100.dp.scaled(50.dp)
            }

            when (val resource = asyncPainterResource(player.image ?: String())) {
                is Resource.Loading, is Resource.Failure -> {

                }
                is Resource.Success -> {
                    Image(
                        modifier = Modifier.width(maxWidth),
                        painter = resource.value,
                        contentDescription = player.name,
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
            Box(modifier = Modifier.matchParentSize().background(Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7F)),
                startY = maxWidth.value / 2
            )))
            Row(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                player.country?.let {
                    Tooltip(
                        tooltip = {
                            Text(
                                modifier = Modifier.clip(
                                    RoundedCornerShape(4.dp)
                                ).background(MaterialTheme.colorScheme.surfaceVariant).padding(4.dp),
                                text = it.name,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    ) {
                        Image(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(CountryImage.getByCode(it.code)),
                            contentDescription = it.name,
                            contentScale = ContentScale.Inside
                        )
                    }
                }

                Text(
                    text = player.name,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = contentColorFor(Color.Black.copy(0.7F)),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun TeamIcon(
    team: Team?,
    initialTeam: Home.Team,
    teamId: Number
) {
    val (preferredIcon, fallbackIcon) = if (LocalDarkMode.current) {
        initialTeam.imgDark to initialTeam.imgLight
    } else {
        initialTeam.imgLight to initialTeam.imgDark
    }
    val (suffix, fallbackSuffix) = if (LocalDarkMode.current) {
        "dark" to "light"
    } else {
        "light" to "dark"
    }

    when (val resource = asyncPainterResource(preferredIcon)) {
        is Resource.Loading -> {
            Spacer(modifier = Modifier.size(56.dp))
        }
        is Resource.Success -> {
            loadImageScheme("$teamId-$suffix", resource.value)
            Image(
                modifier = Modifier.size(56.dp),
                painter = resource.value,
                contentDescription = team?.name ?: initialTeam.name,
                contentScale = ContentScale.FillBounds
            )
        }
        is Resource.Failure -> {
            when (val fallbackResource = asyncPainterResource(fallbackIcon)) {
                is Resource.Loading -> {
                    Spacer(modifier = Modifier.size(56.dp))
                }
                is Resource.Success -> {
                    loadImageScheme("$teamId-$fallbackSuffix", fallbackResource.value)
                    Image(
                        modifier = Modifier.size(56.dp),
                        painter = fallbackResource.value,
                        contentDescription = team?.name ?: initialTeam.name,
                        contentScale = ContentScale.FillBounds
                    )
                }
                is Resource.Failure -> {
                    Icon(
                        modifier = Modifier.size(56.dp),
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = team?.name ?: initialTeam.name,
                    )
                }
            }
        }
    }
}