package io.tujh.imago.presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Stable
interface StateComponent<A, S> : Component<A, S, Nothing> {
    @Composable
    override fun Event(event: Nothing) = Unit
}

interface StateModel<A, S> : Model<A, S, Nothing> {
    override val event: Flow<Nothing>
        get() = emptyFlow()
}