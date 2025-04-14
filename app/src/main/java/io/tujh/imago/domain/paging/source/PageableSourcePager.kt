package io.tujh.imago.domain.paging.source

import io.tujh.imago.domain.paging.paginator.LoadState
import kotlinx.coroutines.flow.Flow

interface PageableSourcePager<T : Any> {
    fun paginate(position: Flow<Int>): Flow<PagedElements<T>>

    suspend fun refresh()
}

data class PagedElements<T : Any>(
    val elements: List<T>,
    val loadState: LoadState,
)