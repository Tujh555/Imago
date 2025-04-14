package io.tujh.imago.presentation.base

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface StateHolder<T> {
    val state: StateFlow<T>

    fun update(block: (T) -> T)
}

fun <T> StateHolder(initialState: T): StateHolder<T> = object : StateHolder<T> {
    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<T> = _state.asStateFlow()

    override fun update(block: (T) -> T) = _state.update(block)
}