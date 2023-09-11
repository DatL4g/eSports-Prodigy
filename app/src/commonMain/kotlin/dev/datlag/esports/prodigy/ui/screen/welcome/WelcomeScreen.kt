package dev.datlag.esports.prodigy.ui.screen.welcome

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.common.launchMain
import dev.datlag.esports.prodigy.ui.custom.PagerIndicator
import dev.datlag.esports.prodigy.ui.theme.LeftRoundedShape
import dev.datlag.esports.prodigy.ui.theme.RightRoundedShape
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.common.calculateCurrentOffsetForPage
import dev.datlag.esports.prodigy.ui.theme.LocalDarkMode
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlin.math.absoluteValue
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(component: WelcomeComponent) {
    val state = rememberPagerState {
        3
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = state,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val contentModifier = Modifier.align(Alignment.Center).padding(bottom = 32.dp)

                when (index) {
                    0 -> FirstPage(contentModifier)
                    1 -> SecondPage(contentModifier)
                    else -> ThirdPage(contentModifier)
                }
            }
        }
        var buttonHeight by remember { mutableStateOf(0) }

        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 16.dp).onSizeChanged {
                buttonHeight = it.height
            },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val hasPrevious = state.currentPage > 0
            val lastPage = state.currentPage == state.pageCount - 1
            val scope = rememberCoroutineScope()

            if (hasPrevious) {
                FilledTonalButton(
                    onClick = {
                        scope.launchMain {
                            state.animateScrollToPage(
                                page = state.currentPage - 1
                            )
                        }
                    },
                    shape = RightRoundedShape(0.dp),
                ) {
                    Text(text = "Back")
                }
            }
            Spacer(modifier = Modifier.weight(1F))
            Button(
                onClick = {
                    if (lastPage) {
                        component.finish()
                    } else {
                        scope.launchMain {
                            state.animateScrollToPage(
                                page = state.currentPage + 1
                            )
                        }
                    }
                },
                shape = LeftRoundedShape(0.dp)
            ) {
                if (lastPage) {
                    Text(text = "Finish")
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Icon(
                        imageVector = Icons.Default.DoubleArrow,
                        contentDescription = "Finish"
                    )
                } else {
                    Text(text = "Next")
                }
            }
        }
        PagerIndicator(
            state = state,
            pageCount = state.pageCount,
            indicatorColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun FirstPage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val res = if (LocalDarkMode.current) {
            SharedRes.images.dreamer_dark
        } else {
            SharedRes.images.dreamer_light
        }

        Image(
            modifier = Modifier.fillMaxWidth(0.5F),
            painter = painterResource(res),
            contentDescription = "Welcome",
            alignment = Alignment.Center,
            contentScale = ContentScale.FillWidth
        )

        Text(
            text = buildAnnotatedString {
                append("Welcome to ")
                withStyle(
                    SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                ) {
                    append(stringResource(SharedRes.strings.app_name))
                }
                append(", the place where you get all your eSports information in one place.")
            },
            modifier = Modifier.fillMaxWidth(0.7F),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SecondPage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val res = if (LocalDarkMode.current) {
            SharedRes.images.fans_dark
        } else {
            SharedRes.images.fans_light
        }

        Image(
            modifier = Modifier.fillMaxWidth(0.5F),
            painter = painterResource(res),
            contentDescription = "Fans",
            alignment = Alignment.Center,
            contentScale = ContentScale.FillWidth
        )

        Text(
            text = "View match statistics, history and celebrate with your favorite team.\nExplore a unique experience that promises to redefine the way you rejoice and make memories like never before.",
            modifier = Modifier.fillMaxWidth(0.7F),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ThirdPage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val res = if (LocalDarkMode.current) {
            SharedRes.images.performance_overview_dark
        } else {
            SharedRes.images.performance_overview_light
        }

        Image(
            modifier = Modifier.fillMaxWidth(0.5F),
            painter = painterResource(res),
            contentDescription = "Performance",
            alignment = Alignment.Center,
            contentScale = ContentScale.FillWidth
        )

        Text(
            text = "Our app allows you to track your or your friends performance, fostering a sense of improvement and incentive through every play.",
            modifier = Modifier.fillMaxWidth(0.7F),
            textAlign = TextAlign.Center
        )
    }
}