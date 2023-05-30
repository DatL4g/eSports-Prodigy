package dev.datlag.esports.prodigy.ui.screen.home.info.carousel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.ui.theme.DiagonalShape

@Composable
fun TeamFightPreview() {
    Row(
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = "Team A"
            )
            Text(
                text = "Team A",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(
                    horizontal = 12.dp,
                    vertical = 4.dp
                )
            )
        }
        Text(
            text = "VS",
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(
                horizontal = 12.dp,
                vertical = 4.dp
            )
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = "Team B"
            )
            Text(
                text = "Team B",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(
                    horizontal = 12.dp,
                    vertical = 4.dp
                )
            )
        }
    }
}