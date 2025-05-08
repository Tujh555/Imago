package io.tujh.imago.data.repository

import io.tujh.imago.domain.paging.source.PageableSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class StateSource<K : Any, T : Any>(
    private val getKey: (T) -> String,
    private val doFetch: suspend (K) -> Result<List<T>>
) : PageableSource<K, T> {
    private val items = MutableStateFlow(emptyList<T>())

    override fun get() = items

    override suspend fun resetTo(key: K) = doFetch(key).onSuccess { items.value = it }

    override suspend fun fetch(key: K) = doFetch(key).onSuccess { data ->
        items.update { it.plus(data).distinctBy(getKey) }
    }
}