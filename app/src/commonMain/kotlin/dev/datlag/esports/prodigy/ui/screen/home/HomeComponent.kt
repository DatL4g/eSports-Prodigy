package dev.datlag.esports.prodigy.ui.screen.home

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import dev.datlag.esports.prodigy.ui.navigation.Component
import dev.icerock.moko.resources.StringResource

interface HomeComponent : Component {

    val childStack: Value<ChildStack<*, Component>>
    val pagerItems: List<PagerItem>
    val selectedPage: Value<Int>

    fun navigate(key: Int)

    data class PagerItem(
        internal val key: Int,
        val label: StringResource,
        val icon: Any
    )
}