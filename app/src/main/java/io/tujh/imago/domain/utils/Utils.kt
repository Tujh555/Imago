package io.tujh.imago.domain.utils

import kotlinx.coroutines.delay
import java.time.Instant

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