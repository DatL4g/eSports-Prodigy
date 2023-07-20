package dev.datlag.esports.prodigy.ui.screen.home.info

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.other.Constants
import dev.datlag.esports.prodigy.ui.LocalCelebrity
import dev.datlag.esports.prodigy.ui.LocalCommonizer
import dev.datlag.esports.prodigy.ui.LocalWindowSize
import dev.datlag.esports.prodigy.ui.WindowSize
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

@Composable
fun InfoView(component: InfoComponent) {
    Column {
        val celebrity = LocalCelebrity.current
        val commented by component.commented.collectAsStateSafe { false }
        var displayCard by remember(commented) { mutableStateOf(!commented) }
        val commonizer = LocalCommonizer.current

        if (celebrity != null && displayCard) {
            val (shape, padding) = when (LocalWindowSize.current) {
                is WindowSize.COMPACT -> RectangleShape to 0.dp
                else -> CardDefaults.shape to 16.dp
            }

            Card(
                modifier = Modifier.padding(padding).fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = shape
            ) {
                Row(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val list = celebrity.nicknames.shuffled()
                    val name by flow {
                        var pos = 0

                        while (true) {
                            delay(1000)
                            emit(list[pos])

                            if (pos + 1 < list.size) {
                                pos++
                            } else {
                                pos = 0
                            }
                        }
                    }.collectAsStateSafe { list.first() }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ){
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(text = "Hey")
                            AnimatedText(
                                name = "$name!",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "How about you leave a comment on my profile"
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.align(Alignment.End).padding(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            displayCard = false
                        }
                    ) {
                        Text(text = "Close")
                    }
                    TextButton(
                        onClick = {
                            commonizer.openInBrowser(Constants.STEAM_PROFILE)
                            component.commented()
                            displayCard = false
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(SharedRes.images.steam),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            text = "Sure!",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        component.deviceView.render()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedText(
    name: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null
) {
    var oldName by remember { mutableStateOf(name) }
    SideEffect {
        oldName = name
    }
    val target = if (name == oldName) {
        oldName
    } else {
        name
    }
    AnimatedContent(
        targetState = target,
        transitionSpec = {
            slideInVertically { it } with slideOutVertically { -it }
        }
    ) {
        Text(
            text = it,
            softWrap = false,
            fontWeight = fontWeight
        )
    }
}