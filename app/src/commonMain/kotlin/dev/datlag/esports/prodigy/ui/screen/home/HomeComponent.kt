package dev.datlag.esports.prodigy.ui.screen.home

import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import dev.datlag.esports.prodigy.ui.navigation.Component
import dev.icerock.moko.resources.StringResource

interface HomeComponent : Component {

    val pagerItems: List<PagerItem>

    fun navigateToUser()

    val pages: Value<ChildPages<*, Component>>
    fun selectPage(index: Int)

    data class PagerItem(
        internal val key: Int,
        val label: StringResource,
        val icon: Any
    )
}