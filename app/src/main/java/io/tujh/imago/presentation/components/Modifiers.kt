package io.tujh.imago.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.screenPadding() = padding(horizontal = 16.dp)

inline fun Modifier.applyIf(condition: () -> Boolean, block: Modifier.() -> Modifier): Modifier {
    if (condition()) {
        return this.block()
    }

    return this
}

inline fun Modifier.applyIf(condition: Boolean, block: Modifier.() -> Modifier) =
    applyIf({ condition }, block)

inline fun <T> Modifier.applyWith(item: T, block: T.(Modifier) -> Modifier): Modifier {
    val initial = this
    return with(item) { block(initial) }
}

fun <T> Modifier.applyNotNull(item: T?, block: Modifier.(T) -> Modifier): Modifier {
    if (item == null) {
        return this
    }

    return this.block(item)
}