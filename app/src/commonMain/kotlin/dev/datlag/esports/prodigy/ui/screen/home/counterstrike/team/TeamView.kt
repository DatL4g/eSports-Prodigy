package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.team

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.model.hltv.Team
import dev.datlag.esports.prodigy.ui.loadImageScheme
import dev.datlag.esports.prodigy.ui.theme.LocalDarkMode
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource

@Composable
fun TeamView(component: TeamComponent) {
    val team by component.team.collectAsStateSafe { null }
    val teamId = remember(team) { team?.id ?: component.initialTeam.id }

    SchemeTheme(teamId) {
        LazyColumn {
            item {
                TeamIcon(team, component.initialTeam, teamId)
            }
            item {
                Button(
                    onClick = {

                    }
                ) {
                    Text(
                        text = "Sample button"
                    )
                }
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

    when (val resource = asyncPainterResource(preferredIcon)) {
        is Resource.Loading -> {
            Spacer(modifier = Modifier.size(56.dp))
        }
        is Resource.Success -> {
            loadImageScheme(teamId, resource.value)
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
                    loadImageScheme(teamId, fallbackResource.value)
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