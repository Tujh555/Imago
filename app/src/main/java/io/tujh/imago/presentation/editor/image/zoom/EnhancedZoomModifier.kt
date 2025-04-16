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

fun Modifier.enhancedZoom(
    key: Any? = Unit,
    clip: Boolean = true,
    enhancedZoomState: EnhancedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = enhancedZoomState.DefaultOnDoubleTap,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }
        val boundPan = enhancedZoomState.limitPan && !enhancedZoomState.rotatable
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key) {
            enhancedZoomState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureStart = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureStart()
                    }
                    onGestureStart?.invoke(enhancedZoomState.enhancedZoomData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureEnd {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val currentZoom = enhancedZoomState.zoom
                    val currentPan = enhancedZoomState.pan
                    val currentRotation = enhancedZoomState.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        enhancedZoomState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    onGesture?.invoke(enhancedZoomState.enhancedZoomData)
                    if (gestureEnabled) {
                        mainPointer.consume()
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key) {
            enhancedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        enhancedZoomState.onDoubleTap(zoom = newZoom) {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(enhancedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "enhancedZoom"
        // add name and value of each argument
        properties["key"] = key
        properties["enabled"] = enabled
        properties["clip"] = clip
        properties["onDown"] = onGestureStart
        properties["onMove"] = onGesture
        properties["onUp"] = onGestureEnd
    }
)

fun Modifier.enhancedZoom(
    key1: Any?,
    key2: Any?,
    clip: Boolean = true,
    enhancedZoomState: EnhancedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = enhancedZoomState.DefaultOnDoubleTap,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null,
) = composed(

    factory = {
        val coroutineScope = rememberCoroutineScope()
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }
        val boundPan = enhancedZoomState.limitPan && !enhancedZoomState.rotatable
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key1, key2) {
            enhancedZoomState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureStart = {
                    onGestureStart?.invoke(enhancedZoomState.enhancedZoomData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureEnd {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val enhancedZoomData = enhancedZoomState.enhancedZoomData
                    val currentZoom = enhancedZoomData.zoom
                    val currentPan = enhancedZoomData.pan
                    val currentRotation = enhancedZoomData.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        enhancedZoomState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    onGesture?.invoke(enhancedZoomState.enhancedZoomData)

                    if (gestureEnabled) {
                        mainPointer.consume()
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key1, key2) {
            enhancedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        enhancedZoomState.onDoubleTap(zoom = newZoom) {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(enhancedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = {
        name = "enhancedZoom"
        properties["key1"] = key1
        properties["key2"] = key2
        properties["clip"] = clip
        properties["enabled"] = enabled
        properties["zoomOnDoubleTap"] = zoomOnDoubleTap
        properties["onDown"] = onGestureStart
        properties["onMove"] = onGesture
        properties["onUp"] = onGestureEnd
    }
)

fun Modifier.enhancedZoom(
    vararg keys: Any?,
    clip: Boolean = true,
    enhancedZoomState: EnhancedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = enhancedZoomState.DefaultOnDoubleTap,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }
        val boundPan = enhancedZoomState.limitPan && !enhancedZoomState.rotatable
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(*keys) {
            enhancedZoomState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureStart = {
                    onGestureStart?.invoke(enhancedZoomState.enhancedZoomData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureEnd {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val enhancedZoomData = enhancedZoomState.enhancedZoomData
                    val currentZoom = enhancedZoomData.zoom
                    val currentPan = enhancedZoomData.pan
                    val currentRotation = enhancedZoomData.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        enhancedZoomState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    onGesture?.invoke(enhancedZoomState.enhancedZoomData)

                    if (gestureEnabled) {
                        mainPointer.consume()
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(*keys) {
            enhancedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        enhancedZoomState.onDoubleTap(zoom = newZoom) {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(enhancedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "enhancedZoom"
        properties["keys"] = keys
        properties["clip"] = clip
        properties["onDown"] = onGestureStart
        properties["onMove"] = onGesture
        properties["onUp"] = onGestureEnd
    }
)

val DefaultEnabled = { _: Float, _: Offset, _: Float ->
    true
}

val DefaultOnDoubleTap: (ZoomLevel) -> Float
    get() = { zoomLevel: ZoomLevel ->
        when (zoomLevel) {
            ZoomLevel.Min -> 1f
            ZoomLevel.Mid -> 2f
            ZoomLevel.Max -> 3f
        }
    }

val BaseEnhancedZoomState.DefaultOnDoubleTap: (ZoomLevel) -> Float
    get() = { zoomLevel: ZoomLevel ->
        when (zoomLevel) {
            ZoomLevel.Min -> 1f.coerceAtMost(zoomMin)
            ZoomLevel.Mid -> 3f.coerceIn(zoomMin, zoomMax)
            ZoomLevel.Max -> 5f.coerceAtLeast(zoomMax)
        }
    }
