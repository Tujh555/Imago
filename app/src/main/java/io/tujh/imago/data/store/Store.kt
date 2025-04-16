package io.tujh.imago.data.store

import kotlinx.coroutines.flow.Flow

interface Store<T> {
    val data: Flow<T?>

    suspend fun update(item: T)

    suspend fun clear()
}