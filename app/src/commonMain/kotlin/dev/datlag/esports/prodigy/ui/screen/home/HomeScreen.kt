package dev.datlag.esports.prodigy.ui.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.pages.Pages
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.datlag.esports.prodigy.ui.custom.ExpandedPages
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.datlag.esports.prodigy.common.onClick

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun HomeScreen(component: HomeComponent) {
    val dialogState by component.dialog.subscribeAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (calculateWindowSizeClass().widthSizeClass) {
            WindowWidthSizeClass.Compact -> CompactScreen(component)
            WindowWidthSizeClass.Medium -> MediumScreen(component)
            WindowWidthSizeClass.Expanded -> ExpandedScreen(component)
        }

        dialogState.child?.also { (_, instance) ->
            instance.render()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalDecomposeApi::class)
@Composable
fun CompactScreen(
    component: HomeComponent
) {
    var selectedPage by remember { mutableStateOf(0) }

    Scaffold(
        floatingActionButton = {
            ExtensionFAB(component)
        },
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalDecomposeApi::class)
@Composable
fun MediumScreen(
    component: HomeComponent
) {
    var selectedPage by remember { mutableStateOf(0) }

    Scaffold(
        floatingActionButton = {
            ExtensionFAB(component)
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalDecomposeApi::class)
@Composable
fun ExpandedScreen(
    component: HomeComponent
) {
    var selectedPage by remember { mutableStateOf(0) }

    Scaffold(
        floatingActionButton = {
            ExtensionFAB(component)
        }
    ) {
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

@Composable
private fun ExtensionFAB(component: HomeComponent) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        var showOtherFABs by remember { mutableStateOf(false) }

        AnimatedVisibility(
            visible = showOtherFABs,
            enter = scaleIn(
                animationSpec = bouncySpring(),
                transformOrigin = TransformOrigin(1F, 0.5F)
            ) + fadeIn(
                animationSpec = bouncySpring()
            ),
            exit = scaleOut(
                transformOrigin = TransformOrigin(1F, 0.5F)
            ) + fadeOut(
                animationSpec = bouncySpring()
            )
        ) {
            LabelFAB(
                label = "Analyze DXVK State-Cache",
                onClick = {
                    showOtherFABs = false
                    component.showDialog(DialogConfig.AnalyzeDXVK)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.DataUsage,
                    contentDescription = null
                )
            }
        }

        FloatingActionButton(
            onClick = {
                showOtherFABs = !showOtherFABs
            }
        ) {
            Icon(
                imageVector = Icons.Default.Extension,
                contentDescription = "Extension"
            )
        }
    }
}

@Composable
private fun LabelFAB(label: String, onClick: () -> Unit, icon: @Composable () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.onClick {
                onClick()
            },
            tonalElevation = 8.dp,
            shadowElevation = 4.dp,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = label,
                maxLines = 1
            )
        }

        SmallFloatingActionButton(
            onClick = {
                onClick()
            }
        ) {
            icon()
        }
    }
}

private fun <T> bouncySpring() = spring<T>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)