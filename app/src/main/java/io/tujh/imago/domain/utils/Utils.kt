package io.tujh.imago.domain.utils

import io.tujh.imago.domain.paging.source.PageableSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend inline fun <T> withMinDelay(delay: Long = 600, block: () -> T): T {
    val startTime = System.currentTimeMillis()
    val result = block()
    val elapsedTime = System.currentTimeMillis() - startTime
    val additionalDelay = (delay - elapsedTime).coerceIn(0, delay)
    delay(additionalDelay)
    return result
}

fun String?.toInstantOrNow(): Instant =
    runCatching { Instant.parse(this!!) }.getOrDefault(Instant.now())

fun Result<*>.map() = map { }

fun <K : Any, T : Any, R : Any> PageableSource<K, T>.map(block: (T) -> R): PageableSource<K, R> {
    val underlying = this
    return object : PageableSource<K, R> {
        override fun get() = underlying.get().map { list -> list.map(block) }

        override suspend fun resetTo(key: K) = underlying.resetTo(key).map { list -> list.map(block) }

        override suspend fun fetch(key: K) = underlying.fetch(key).map { list -> list.map(block) }
    }
}

inline fun <T, R> Result<T>.flatMap(block: (T) -> R): Result<R> = if (isSuccess) {
    kotlin.runCatching { getOrThrow() }.map(block)
} else {
    Result.failure(exceptionOrNull() ?: RuntimeException())
}

fun <T> runBlockingWithCancellation(
    isCancelled: () -> Boolean,
    context: CoroutineContext = EmptyCoroutineContext,
    interval: Long = 16L,
    block: suspend CoroutineScope.() -> T
) = runBlocking(context) {
    val outer = this
    val job = launch {
        while (isActive) {
            delay(interval)
            if (isCancelled()) {
                outer.cancel()
            }
        }
    }

    block().also {
        job.cancel()
    }
}