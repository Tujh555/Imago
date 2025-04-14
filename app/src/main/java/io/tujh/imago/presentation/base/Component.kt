package io.tujh.imago.presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Stable
interface Component<A, S, E> : Screen {
    @Composable
    override fun Content() {
        val model = model()

        val state by model.state.collectAsState()
        Content(state) { model.onAction(it) }

        val event by model.event.collectAsState(null)
        event?.let { Event(it) }
    }

    @Composable
    fun Content(state: S, onAction: (A) -> Unit)

    @Composable
    fun Event(event: E)

    @Composable
    fun model(): Model<A, S, E>
}

@Stable
interface Model<A, S, E> : ScreenModel {
    val state: StateFlow<S>
    val event: Flow<E>

    fun onAction(action: A)
}

fun CoroutineScope.io(block: suspend CoroutineScope.() -> Unit) =
    launch(Dispatchers.IO, block = block)