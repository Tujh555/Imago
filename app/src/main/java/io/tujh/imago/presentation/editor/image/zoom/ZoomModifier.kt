package io.tujh.imago.presentation.editor.image.zoom

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.smarttoolfactory.gesture.detectTransformGestures
import io.tujh.imago.presentation.editor.image.util.calculateZoom
import io.tujh.imago.presentation.editor.image.util.update
import kotlinx.coroutines.launch

fun Modifier.zoom(
    key: Any? = Unit,
    consume: Boolean = true,
    clip: Boolean = true,
    zoomState: ZoomState,
    onGestureStart: ((ZoomData) -> Unit)? = null,
    onGesture: ((ZoomData) -> Unit)? = null,
    onGestureEnd: ((ZoomData) -> Unit)? = null
) = composed(
    factory = {
        val coroutineScope = rememberCoroutineScope()
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }
        val boundPan = zoomState.limitPan && !zoomState.rotatable
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key) {
            zoomState.size = this.size

            detectTransformGestures(
                consume = consume,
                onGestureStart = {
                    onGestureStart?.invoke(zoomState.zoomData)
                },
                onGestureEnd = {
                    onGestureEnd?.invoke(zoomState.zoomData)
                },
                onGesture = { centroid, pan, zoom, rotation, _, _ ->

                    coroutineScope.launch {
                        zoomState.updateZoomState(
                            centroid = centroid,
                            panChange = pan,
                            zoomChange = zoom,
                            rotationChange = rotation
                        )
                    }

                    onGesture?.invoke(zoomState.zoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key) {
            zoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {

                    val (newZoomLevel, newZoom) = calculateZoom(
                        zoomLevel = zoomLevel,
                        initial = zoomState.zoomInitial,
                        min = zoomState.zoomMin,
                        max = zoomState.zoomMax
                    )

                    zoomLevel = newZoomLevel

                    coroutineScope.launch {
                        zoomState.resetWithAnimation(
                            zoom = newZoom,
                            rotation = zoomState.rotationInitial
                        )
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(zoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(transformModifier)
                .then(tapModifier)
                .then(graphicsModifier)

        )
    },
    inspectorInfo = {
        name = "zoom"
        properties["key"] = key
        properties["clip"] = clip
        properties["consume"] = consume
        properties["zoomState"] = zoomState
        properties["onGestureStart"] = onGestureStart
        properties["onGesture"] = onGesture
        properties["onGestureEnd"] = onGestureEnd
    }
)

fun Modifier.zoom(
    key1: Any?,
    key2: Any?,
    consume: Boolean = true,
    clip: Boolean = true,
    zoomState: ZoomState,
    onGestureStart: ((ZoomData) -> Unit)? = null,
    onGesture: ((ZoomData) -> Unit)? = null,
    onGestureEnd: ((ZoomData) -> Unit)? = null
) = composed(
    factory = {
        val coroutineScope = rememberCoroutineScope()
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }
        val boundPan = zoomState.limitPan && !zoomState.rotatable
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key1, key2) {
            zoomState.size = this.size
            detectTransformGestures(
                consume = consume,
                onGestureStart = {
                    onGestureStart?.invoke(zoomState.zoomData)
                },
                onGestureEnd = {
                    onGestureEnd?.invoke(zoomState.zoomData)
                },
                onGesture = { centroid, pan, zoom, rotation, _, _ ->

                    coroutineScope.launch {
                        zoomState.updateZoomState(
                            centroid = centroid,
                            panChange = pan,
                            zoomChange = zoom,
                            rotationChange = rotation
                        )
                    }

                    onGesture?.invoke(zoomState.zoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key1, key2) {
            zoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {

                    val (newZoomLevel, newZoom) = calculateZoom(
                        zoomLevel = zoomLevel,
                        initial = zoomState.zoomInitial,
                        min = zoomState.zoomMin,
                        max = zoomState.zoomMax
                    )

                    zoomLevel = newZoomLevel

                    coroutineScope.launch {
                        zoomState.resetWithAnimation(
                            zoom = newZoom,
                            rotation = zoomState.rotationInitial
                        )
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(zoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(transformModifier)
                .then(tapModifier)
                .then(graphicsModifier)

        )
    },
    inspectorInfo = {
        name = "zoom"
        properties["key1"] = key1
        properties["key2"] = key2
        properties["clip"] = clip
        properties["consume"] = consume
        properties["zoomState"] = zoomState
        properties["onGestureStart"] = onGestureStart
        properties["onGesture"] = onGesture
        properties["onGestureEnd"] = onGestureEnd
    }
)

fun Modifier.zoom(
    vararg keys: Any?,
    consume: Boolean = true,
    clip: Boolean = true,
    zoomState: ZoomState,
    onGestureStart: ((ZoomData) -> Unit)? = null,
    onGesture: ((ZoomData) -> Unit)? = null,
    onGestureEnd: ((ZoomData) -> Unit)? = null
) = composed(
    factory = {
        val coroutineScope = rememberCoroutineScope()
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }
        val boundPan = zoomState.limitPan && !zoomState.rotatable
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(*keys) {
            zoomState.size = this.size
            detectTransformGestures(
                consume = consume,
                onGestureStart = {
                    onGestureStart?.invoke(zoomState.zoomData)
                },
                onGestureEnd = {
                    onGestureEnd?.invoke(zoomState.zoomData)
                },
                onGesture = { centroid, pan, zoom, rotation, _, _ ->

                    coroutineScope.launch {
                        zoomState.updateZoomState(
                            centroid = centroid,
                            panChange = pan,
                            zoomChange = zoom,
                            rotationChange = rotation
                        )
                    }

                    onGesture?.invoke(zoomState.zoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(*keys) {
            zoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {

                    val (newZoomLevel, newZoom) = calculateZoom(
                        zoomLevel = zoomLevel,
                        initial = zoomState.zoomInitial,
                        min = zoomState.zoomMin,
                        max = zoomState.zoomMax
                    )

                    zoomLevel = newZoomLevel

                    coroutineScope.launch {
                        zoomState.resetWithAnimation(
                            zoom = newZoom,
                            rotation = zoomState.rotationInitial
                        )
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(zoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(transformModifier)
                .then(tapModifier)
                .then(graphicsModifier)

        )
    },
    inspectorInfo = {
        name = "zoom"
        properties["keys"] = keys
        properties["clip"] = clip
        properties["consume"] = consume
        properties["zoomState"] = zoomState
        properties["onGestureStart"] = onGestureStart
        properties["onGesture"] = onGesture
        properties["onGestureEnd"] = onGestureEnd
    }
)

fun Modifier.zoom(
    key: Any? = Unit,
    zoomState: ZoomState,
    clip: Boolean = true,
) = zoom(
    key = key,
    clip = clip,
    consume = true,
    zoomState = zoomState,
    onGestureStart = null,
    onGestureEnd = null,
    onGesture = null
)

fun Modifier.zoom(
    key1: Any?,
    key2: Any?,
    zoomState: ZoomState,
    clip: Boolean = true,
) = zoom(
    key1 = key1,
    key2 = key2,
    clip = clip,
    consume = true,
    zoomState = zoomState,
    onGestureStart = null,
    onGestureEnd = null,
    onGesture = null
)

fun Modifier.zoom(
    vararg keys: Any?,
    zoomState: ZoomState,
    clip: Boolean = true,
) = zoom(
    keys = keys,
    clip = clip,
    consume = true,
    zoomState = zoomState,
    onGestureStart = null,
    onGestureEnd = null,
    onGesture = null
)

val ZoomState.zoomData: ZoomData
    get() = ZoomData(
        zoom = zoom,
        pan = pan,
        rotation = rotation
    )
