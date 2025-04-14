package io.tujh.imago.presentation.base

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

interface EventEmitter<E> {
    val event: Flow<E>

    suspend fun emit(event: E)
}

fun <E> EventEmitter(): EventEmitter<E> = object : EventEmitter<E> {
    private val _event = Channel<E>(Channel.CONFLATED)
    override val event = _event.receiveAsFlow()

    override suspend fun emit(event: E) = _event.send(event)
}