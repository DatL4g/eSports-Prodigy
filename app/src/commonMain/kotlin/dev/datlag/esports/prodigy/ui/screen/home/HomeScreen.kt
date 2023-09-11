package dev.datlag.esports.prodigy.ui.screen.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.pages.Pages
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.datlag.esports.prodigy.ui.custom.ExpandedPages
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun HomeScreen(component: HomeComponent) {
    val settingsVisible by component.settingsVisible.subscribeAsState()
    val animationProgress by animateFloatAsState(
        targetValue = if (settingsVisible) 1F else 0F,
        animationSpec = tween()
    )
    val transition = updateTransition(targetState = animationProgress)
    val animatedShape by transition.animateValue(
        TwoWayConverter(
            convertToVector = { AnimationVector1D(0F) },
            convertFromVector = { GenericShape { _, _ -> } }
        )
    ) { progress ->
        GenericShape { size, _ ->
            val centerH = size.width / 2F
            val multiplierW = 1.5F + size.height / size.width

            moveTo(
                x = centerH - centerH * progress * multiplierW,
                y = 0F
            )

            val currentWidth = (centerH * progress * multiplierW * 2.5F)

            cubicTo(
                x1 = centerH - centerH * progress * 1.5f,
                y1 = currentWidth * 0.5f,
                x2 = centerH + centerH * progress * 1.5f,
                y2 = currentWidth * 0.5f,
                x3 = centerH + centerH * progress * multiplierW,
                y3 = 0F
            )

            close()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (calculateWindowSizeClass().widthSizeClass) {
            WindowWidthSizeClass.Compact -> CompactScreen(component)
            WindowWidthSizeClass.Medium -> MediumScreen(component)
            WindowWidthSizeClass.Expanded -> ExpandedScreen(component)
        }

        if (animationProgress > 0F) {
            Surface(
                color = Color.Red,
                modifier = Modifier.fillMaxSize().graphicsLayer {
                    clip = true
                    shape = animatedShape
                }
            ) {  }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CompactScreen(
    component: HomeComponent
) {
    var selectedPage by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                component.pagerItems.forEach { item ->
                    NavigationBarItem(
                        selected = selectedPage == item.key,
                        icon = {
                            NavIcon(item)
                        },
                        onClick = {
                            component.selectPage(item.key)
                        },
                        label = {
                            Text(text = stringResource(item.label))
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Pages(
                pages = component.pages,
                onPageSelected = { index ->
                    component.selectPage(index)
                }
            ) { index, page ->
                selectedPage = index
                page.render()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MediumScreen(
    component: HomeComponent
) {
    var selectedPage by remember { mutableStateOf(0) }

    Scaffold(
        floatingActionButton = {

        }
    ) {
        Row(modifier = Modifier.padding(it)) {
            NavigationRail(
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.weight(1F))
                component.pagerItems.forEach { item ->
                    NavigationRailItem(
                        selected = selectedPage == item.key,
                        icon = {
                            NavIcon(item)
                        },
                        onClick = {
                            component.selectPage(item.key)
                        },
                        label = {
                            Text(text = stringResource(item.label))
                        },
                        alwaysShowLabel = false
                    )
                }
                Spacer(modifier = Modifier.weight(1F))
            }

            ExpandedPages(
                pages = component.pages
            ) { index, page ->
                selectedPage = index
                page.render()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ExpandedScreen(
    component: HomeComponent
) {
    var selectedPage by remember { mutableStateOf(0) }

    Scaffold {
        PermanentNavigationDrawer(
            modifier = Modifier.padding(it),
            drawerContent = {
                PermanentDrawerSheet(
                    drawerShape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 0.dp
                    )
                ) {
                    Spacer(modifier = Modifier.weight(1F))
                    component.pagerItems.forEach { item ->
                        NavigationDrawerItem(
                            icon = {
                                NavIcon(item)
                            },
                            label = {
                                Text(text = stringResource(item.label))
                            },
                            onClick = {
                                component.selectPage(item.key)
                            },
                            selected = selectedPage == item.key
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
                }
            }
        ) {
            Box(
                contentAlignment = Alignment.TopStart
            ) {
                ExpandedPages(
                    pages = component.pages
                ) { index, page ->
                    selectedPage = index
                    page.render()
                }
            }
        }
    }
}

@Composable
fun NavIcon(item: HomeComponent.PagerItem) {
    when (item.icon) {
        is ImageVector -> {
            Icon(
                imageVector = item.icon,
                contentDescription = stringResource(item.label),
                modifier = Modifier.size(24.dp)
            )
        }
        is Painter -> {
            Icon(
                painter = item.icon,
                contentDescription = stringResource(item.label),
                modifier = Modifier.size(24.dp)
            )
        }
        is ImageResource -> {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = stringResource(item.label),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}