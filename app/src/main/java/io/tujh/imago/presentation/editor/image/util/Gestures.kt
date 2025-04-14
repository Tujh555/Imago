package io.tujh.imago.presentation.editor.image.util

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange

@Stable
fun Modifier.motionEvents(key: Any? = null, onEvent: (MotionEvent) -> Unit) = pointerInput(key) {
    awaitEachGesture { awaitDragMotionEvent(onEvent) }
}

private suspend fun AwaitPointerEventScope.awaitDragMotionEvent(onEvent: (MotionEvent) -> Unit) {
    val down = awaitFirstDown()
    onEvent(MotionEvent.Down(down.position))
    if (down.pressed != down.previousPressed) {
        down.consume()
    }
    var pointer = down
    val change = awaitTouchSlopOrCancellation(down.id) { change, _ ->
        if (change.positionChange() != Offset.Zero) {
            change.consume()
        }
    }

    if (change != null) {
        drag(change.id) { input ->
            pointer = input
            onEvent(MotionEvent.Move(pointer.position))
            if (pointer.positionChange() != Offset.Zero) {
                pointer.consume()
            }
        }
    }

    onEvent(MotionEvent.Up(pointer.position))
    if (pointer.pressed != pointer.previousPressed) {
        pointer.consume()
    }
}