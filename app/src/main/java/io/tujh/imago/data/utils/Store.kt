package io.tujh.imago.data.utils

import io.tujh.imago.data.store.Store
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

suspend fun <T> Store<T>.get(): T? = withTimeoutOrNull(1.seconds) {
    data.filterNotNull().first()
}