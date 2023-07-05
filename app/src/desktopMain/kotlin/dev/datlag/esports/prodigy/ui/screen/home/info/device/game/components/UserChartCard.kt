package dev.datlag.esports.prodigy.ui.screen.home.info.device.game.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.game.model.steam.User
import dev.datlag.esports.prodigy.ui.loadImageScheme
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource
import kotlinx.datetime.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.max

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserChartCard(
    user: User,
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
        width > 0 && height > 0 -> then(Modifier.defaultMinSize(
            minWidth = widthDp,
            minHeight = heightDp
        ))
        width > 0 -> then(Modifier.defaultMinSize(
            minWidth = widthDp
        ))
        height > 0 -> then(Modifier.defaultMinSize(
            minHeight = heightDp
        ))
        else -> then(Modifier)
    }

    SchemeTheme(user.id) {
        OutlinedCard(
            modifier = Modifier.padding(vertical = 2.dp).sameSize().onSizeChanged {
                if (width < it.width || height < it.height) {
                    onSizeChange(
                        max(width, it.width) to max(height, it.height)
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val fallbackColor = remember {
                        Color(
                            red = (0..255).random(),
                            green = (0..255).random(),
                            blue = (0..255).random()
                        )
                    }

                    when (val resource = user.avatarFile?.let { asyncPainterResource(it) }) {
                        null, is Resource.Failure, is Resource.Loading -> {
                            Box(modifier = Modifier.size(56.dp).background(color = fallbackColor, shape = MaterialTheme.shapes.small))
                        }
                        is Resource.Success -> {
                            loadImageScheme(user.id, resource.value)
                            Image(
                                painter = resource.value,
                                contentDescription = user.name,
                                contentScale = ContentScale.FillBounds,
                                alignment = Alignment.Center,
                                modifier = Modifier.size(56.dp).clip(MaterialTheme.shapes.small)
                            )
                        }
                    }

                    Text(
                        text = user.name,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier.padding(vertical = 8.dp)
                        .width(widthDp - 32.dp)
                        .height(DividerDefaults.Thickness)
                        .background(DividerDefaults.color)
                )

                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "ID:",
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Account:",
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Last login:",
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = user.id)
                        Text(text = user.data.accountName)
                        val instant = Instant.fromEpochSeconds(user.data.timestamp)
                        val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

                        Text(text = date.toJavaLocalDate().format(formatter))
                    }
                }
                Button(
                    onClick = {

                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "View Stats")
                }
            }
        }
    }
}