package io.tujh.imago.domain.paging.source

import io.tujh.imago.domain.paging.paginator.LoadState
import io.tujh.imago.domain.paging.paginator.StatePaginator
import io.tujh.imago.domain.utils.withMinDelay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private class PageableSourcePagerImpl<K : Any, T : Any>(
    private val source: PageableSource<K, T>,
    private val paginator: StatePaginator<K, T>,
    private val minDelay: Long,
) : PageableSourcePager<T> {

    private val refreshes = Channel<Int>()

    private val loading = Mutex()

    @Volatile
    private var pagesLoaded = 0

    override fun paginate(position: Flow<Int>): Flow<PagedElements<T>> = channelFlow {
        launch {
            @Suppress("OPT_IN_USAGE")
            refreshes.receiveAsFlow().flatMapLatest { pagesLoaded ->
                position
                    .map { index -> (index + 1) / paginator.limit + 1 }
                    .distinctUntilChanged { oldPage, newPage -> oldPage >= newPage }
                    .filter { pageToLoad -> pageToLoad > pagesLoaded }
            }.collect {
                if (minDelay > 0L) {
                    loading.withLock {
                        withMinDelay(minDelay) {
                            paginator.loadNextItems()
                        }
                    }
                } else {
                    paginator.loadNextItems()
                }.onSuccess { result ->
                    if (result.isNotEmpty()) {
                        pagesLoaded++
                    } else if (paginator.loadState.value == LoadState.Loaded && pagesLoaded == 0) {
                        pagesLoaded = 1
                    }
                }
            }
        }

        refreshes.send(pagesLoaded)

        var previous = paginator.loadState.value
        var previousElements: List<*>? = null

        combine(source.get(), paginator.loadState, ::Pair).runningReduce { prev, curr ->
            val delayLastPage = prev.second != LoadState.Loaded && curr.second == LoadState.Loaded
            val delayNextPage = prev.first.size != curr.first.size
            val delayFirstError =
                prev.second != LoadState.Failed && curr.second == LoadState.Failed
            if (minDelay > 0L && (delayNextPage || delayLastPage || delayFirstError)) {
                loading.withLock { }
            }
            curr
        }.collect { (elements, pagingState) ->
            val prev = previous
            previous = pagingState

            val hasRemainder = elements.size % paginator.limit != 0
            val recordsPages = elements.size / paginator.limit + if (hasRemainder) 1 else 0
            val consecutiveEmpty =
                previousElements != null && previousElements!!.isEmpty() && elements.isEmpty()

            previousElements = elements

            val correctedPagingState = if (prev != LoadState.Loaded &&
                pagingState == LoadState.Loaded &&
                consecutiveEmpty.not() &&
                recordsPages < pagesLoaded
            ) {
                LoadState.Loading
            } else {
                pagingState
            }
            send(PagedElements(elements, correctedPagingState))
        }
    }

    override suspend fun refresh() {
        val start = System.currentTimeMillis()

        val currentState = paginator.loadState.value
        if (currentState == LoadState.Failed || currentState == LoadState.Loaded) {
            paginator.onLoading()
        }

        source.resetTo(paginator.initialKey).onSuccess { elements ->
            if (currentState == LoadState.Loaded && elements.isEmpty()) {
                delay(minDelay - (System.currentTimeMillis() - start))
            }
            paginator.reset(paginator.initialKey)
            val secondPageKey = paginator.getNextKey(elements)
            paginator.reset(secondPageKey)
            pagesLoaded = 1
            refreshes.send(pagesLoaded)
        }.onFailure {
            delay(minDelay - (System.currentTimeMillis() - start))
            paginator.onError()
        }
    }
}

fun <K : Any, T : Any> PageableSourcePager(
    source: PageableSource<K, T>,
    paginator: StatePaginator<K, T>,
    minDelay: Long = 0
): PageableSourcePager<T> = PageableSourcePagerImpl(source, paginator, minDelay)