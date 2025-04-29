package io.tujh.imago.presentation.base

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

@Stable
interface TabComponent<A, S, E> : Tab, Component<A, S, E> {
    override val options @Composable get() = emptyOptions

    val title: String
    val icon: Int
        @DrawableRes get

    companion object {
        private val emptyOptions = TabOptions(0u, "", null)
    }
}

interface StateTabComponent<A, S> : TabComponent<A, S, Nothing> {
    @Composable
    override fun Event(event: Nothing) = Unit
}