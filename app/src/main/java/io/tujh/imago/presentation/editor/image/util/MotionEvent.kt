package io.tujh.imago.presentation.editor.image.util

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

@Immutable
sealed interface MotionEvent {
    val position: Offset

    val x: Float
        get() = position.x
    val y: Float
        get() = position.y

    data object Unspecified : MotionEvent {
        override val position: Offset = Offset.Unspecified

        override fun toString() = "Unspecified"
    }

    @JvmInline
    value class Down(override val position: Offset) : MotionEvent {
        override fun toString() = "Down(position = $position)"
    }

    @JvmInline
    value class Move(override val position: Offset) : MotionEvent {
        override fun toString() = "Move(position = $position)"
    }

    @JvmInline
    value class Up(override val position: Offset) : MotionEvent {
        override fun toString() = "Up(position = $position)"
    }
}