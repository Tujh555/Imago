package io.tujh.imago.presentation.editor.image.zoom

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.debugInspectorInfo
import com.smarttoolfactory.gesture.detectTransformGestures
import io.tujh.imago.presentation.editor.image.util.getNextZoomLevel
import io.tujh.imago.presentation.editor.image.util.update
import kotlinx.coroutines.launch

fun Modifier.animatedZoom(
    vararg keys: Any?,
    clip: Boolean = true,
    animatedZoomState: AnimatedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = animatedZoomState.DefaultOnDoubleTap,
) = composed(
    factory = {
        val coroutineScope = rememberCoroutineScope()
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }
        val boundPan = animatedZoomState.limitPan && !animatedZoomState.rotatable
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(*keys) {
            animatedZoomState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureEnd = {
                    coroutineScope.launch {
                        animatedZoomState.onGestureEnd {
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val currentZoom = animatedZoomState.zoom
                    val currentPan = animatedZoomState.pan
                    val currentRotation = animatedZoomState.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        animatedZoomState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    if (gestureEnabled) {
                        mainPointer.consume()
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(*keys) {
            animatedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        animatedZoomState.onDoubleTap(zoom = newZoom) {}
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(animatedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "animatedZoomState"
        properties["keys"] = keys
        properties["clip"] = clip
        properties["animatedZoomState"] = animatedZoomState
        properties["enabled"] = enabled
        properties["zoomOnDoubleTap"] = zoomOnDoubleTap
    }
)

fun Modifier.animatedZoom(
    key: Any? = Unit,
    clip: Boolean = true,
    animatedZoomState: AnimatedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = animatedZoomState.DefaultOnDoubleTap,
) = composed(

    factory = {
        val coroutineScope = rememberCoroutineScope()
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }
        val boundPan = animatedZoomState.limitPan && !animatedZoomState.rotatable
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key) {
            animatedZoomState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureEnd = {
                    coroutineScope.launch {
                        animatedZoomState.onGestureEnd {
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val currentZoom = animatedZoomState.zoom
                    val currentPan = animatedZoomState.pan
                    val currentRotation = animatedZoomState.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        animatedZoomState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    if (gestureEnabled) {
                        mainPointer.consume()
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key) {
            animatedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        animatedZoomState.onDoubleTap(zoom = newZoom) {}
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(animatedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "animatedZoomState"
        properties["key"] = key
        properties["clip"] = clip
        properties["animatedZoomState"] = animatedZoomState
        properties["enabled"] = enabled
        properties["zoomOnDoubleTap"] = zoomOnDoubleTap
    }
)

fun Modifier.animatedZoom(
    key1: Any? = Unit,
    key2: Any? = Unit,
    clip: Boolean = true,
    animatedZoomState: AnimatedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = animatedZoomState.DefaultOnDoubleTap,
) = composed(

    factory = {
        val coroutineScope = rememberCoroutineScope()
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }
        val boundPan = animatedZoomState.limitPan && !animatedZoomState.rotatable
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key1, key2) {
            animatedZoomState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureEnd = {
                    coroutineScope.launch {
                        animatedZoomState.onGestureEnd {
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val currentZoom = animatedZoomState.zoom
                    val currentPan = animatedZoomState.pan
                    val currentRotation = animatedZoomState.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        animatedZoomState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )

                        if (gestureEnabled) {
                            mainPointer.consume()
                        }
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key1, key2) {
            animatedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        animatedZoomState.onDoubleTap(zoom = newZoom) {}
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(animatedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "animatedZoomState"
        properties["key1"] = key1
        properties["key2"] = key2
        properties["clip"] = clip
        properties["animatedZoomState"] = animatedZoomState
        properties["enabled"] = enabled
        properties["zoomOnDoubleTap"] = zoomOnDoubleTap
    }
)
