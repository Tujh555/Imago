package io.tujh.imago.domain.paging.paginator

import io.tujh.imago.domain.paging.source.PageableSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicBoolean

abstract class Paginator<Key : Any, Item : Any>(val initialKey: Key) {
    private val isMakingRequest = AtomicBoolean(false)

    var currentKey = initialKey
        private set

    fun prepareForLoading(): Boolean =
        isMakingRequest.compareAndSet(false, true)

    suspend fun loadNextItems(): Result<List<Item>> {
        onLoading()
        val result = onRequest(currentKey)
            .onSuccess {
                val nextKey = getNextKey(it)
                val endReached: Boolean
                if (nextKey == null) {
                    endReached = true
                } else {
                    currentKey = nextKey
                    endReached = false
                }
                onSuccess(endReached)
            }.onFailure { onError() }
        isMakingRequest.set(false)
        return result
    }

    suspend fun reset(key: Key?) {
        onSuccess(key == null)
        currentKey = key ?: return
    }

    abstract suspend fun getNextKey(list: List<Item>): Key?

    abstract suspend fun onError()

    abstract suspend fun onLoading()

    protected abstract suspend fun onRequest(nextKey: Key): Result<List<Item>>

    protected abstract suspend fun onSuccess(endReached: Boolean)
}

abstract class StatePaginator<Key : Any, Item : Any>(
    initialKey: Key,
    val limit: Int
) : Paginator<Key, Item>(initialKey) {
    private val _loadState = MutableStateFlow(LoadState.Initial)

    val loadState = _loadState.asStateFlow()

    override suspend fun onLoading() {
        _loadState.update { LoadState.Loading }
    }

    override suspend fun onError() {
        _loadState.update { LoadState.Failed }
    }

    override suspend fun onSuccess(endReached: Boolean) {
        val newLoadState = if (endReached) LoadState.Loaded else LoadState.Initial
        _loadState.update { newLoadState }
    }
}

fun <T : Any, K : Any> statePaginator(
    initialKey: K,
    limit: Int,
    pageableSource: PageableSource<K, T>,
    getKey: (T) -> K
): StatePaginator<K, T> = object : StatePaginator<K, T>(initialKey, limit) {
    override suspend fun getNextKey(list: List<T>): K? {
        if (list.size < limit) {
            return null
        }

        return list.lastOrNull()?.let(getKey)
    }

    override suspend fun onRequest(nextKey: K): Result<List<T>> = pageableSource.fetch(nextKey)
}