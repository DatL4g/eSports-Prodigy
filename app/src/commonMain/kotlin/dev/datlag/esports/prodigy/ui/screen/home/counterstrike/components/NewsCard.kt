package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.model.hltv.News
import dev.datlag.esports.prodigy.ui.theme.CountryImage
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.max

@Composable
fun NewsCard(
    news: News,
    width: Int,
    height: Int,
    onSizeChange: (Pair<Int, Int>) -> Unit
) {
    val widthDp = with(LocalDensity.current) {
        width.toDp()
    }
    val heightDp = with(LocalDensity.current) {
        height.toDp()
    }
    fun Modifier.sameSize() = when {
        width > 0 && height > 0 -> then(
            Modifier.defaultMinSize(
            minWidth = widthDp,
            minHeight = heightDp
        ))
        width > 0 -> then(
            Modifier.defaultMinSize(
            minWidth = widthDp
        ))
        height > 0 -> then(
            Modifier.defaultMinSize(
            minHeight = heightDp
        ))
        else -> then(Modifier)
    }

    ElevatedCard(
        modifier = Modifier.sameSize().onSizeChanged {
            if (width < it.width || height > it.height) {
                onSizeChange(
                    max(width, it.width) to max(height, it.height)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = news.title,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
            Row {
                Icon(
                    painter = painterResource(CountryImage.getByCode(news.country.code)),
                    modifier = Modifier.size(24.dp).clip(CircleShape),
                    contentDescription = news.country.name,
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.weight(1F))
                if (news.date > 0L) {
                    Text(text = Instant.fromEpochSeconds(news.date).toLocalDateTime(TimeZone.currentSystemDefault()).toString())
                }
            }
        }
    }
}