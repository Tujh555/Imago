package io.tujh.imago.domain.paging.source

import kotlinx.coroutines.flow.Flow

interface PageableSource<K : Any, T : Any> {
    fun get(): Flow<List<T>>

    suspend fun fetch(key: K): Result<List<T>>

    suspend fun resetTo(key: K): Result<List<T>>
}