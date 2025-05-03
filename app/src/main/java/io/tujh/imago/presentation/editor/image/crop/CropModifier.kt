package io.tujh.imago.presentation.editor.image.crop

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.LaunchedEffect
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
import com.smarttoolfactory.gesture.detectMotionEventsAsList
import com.smarttoolfactory.gesture.detectTransformGestures
import io.tujh.imago.presentation.editor.image.util.getNextZoomLevel
import io.tujh.imago.presentation.editor.image.util.update
import io.tujh.imago.presentation.editor.image.zoom.ZoomLevel
import kotlinx.coroutines.launch

fun Modifier.crop(
    vararg keys: Any?,
    cropState: CropState,
    zoomOnDoubleTap: (ZoomLevel) -> Float = cropState.DefaultOnDoubleTap,
    onDown: ((CropData) -> Unit)? = null,
    onMove: ((CropData) -> Unit)? = null,
    onUp: ((CropData) -> Unit)? = null,
    onGestureStart: ((CropData) -> Unit)? = null,
    onGesture: ((CropData) -> Unit)? = null,
    onGestureEnd: ((CropData) -> Unit)? = null
) = composed {
    LaunchedEffect(key1 = cropState){
        cropState.init()
    }

    val coroutineScope = rememberCoroutineScope()

    var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

    val transformModifier = Modifier.pointerInput(*keys) {
        detectTransformGestures(
            consume = false,
            onGestureStart = {
                onGestureStart?.invoke(cropState.cropData)
            },
            onGestureEnd = {
                coroutineScope.launch {
                    cropState.onGestureEnd {
                        onGestureEnd?.invoke(cropState.cropData)
                    }
                }
            },
            onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->
                coroutineScope.launch {
                    cropState.onGesture(
                        centroid = centroid,
                        panChange = pan,
                        zoomChange = zoom,
                        rotationChange = rotate,
                        mainPointer = mainPointer,
                        changes = pointerList
                    )
                }
                onGesture?.invoke(cropState.cropData)
                mainPointer.consume()
            }
        )
    }

    val tapModifier = Modifier.pointerInput(*keys) {
        detectTapGestures(
            onDoubleTap = { offset: Offset ->
                coroutineScope.launch {
                    zoomLevel = getNextZoomLevel(zoomLevel)
                    val newZoom = zoomOnDoubleTap(zoomLevel)
                    cropState.onDoubleTap(
                        offset = offset,
                        zoom = newZoom
                    ) {
                        onGestureEnd?.invoke(cropState.cropData)
                    }
                }
            }
        )
    }

    val touchModifier = Modifier.pointerInput(*keys) {
        detectMotionEventsAsList(
            onDown = {
                coroutineScope.launch {
                    cropState.onDown(it)
                    onDown?.invoke(cropState.cropData)
                }
            },
            onMove = {
                coroutineScope.launch {
                    cropState.onMove(it)
                    onMove?.invoke(cropState.cropData)
                }
            },
            onUp = {
                coroutineScope.launch {
                    cropState.onUp(it)
                    onUp?.invoke(cropState.cropData)
                }
            }
        )
    }

    val graphicsModifier = Modifier.graphicsLayer { update(cropState) }

    then(
        clipToBounds()
            .then(tapModifier)
            .then(transformModifier)
            .then(touchModifier)
            .then(graphicsModifier)
    )
}

val CropState.DefaultOnDoubleTap: (ZoomLevel) -> Float
    get() = { zoomLevel: ZoomLevel ->
        when (zoomLevel) {
            ZoomLevel.Min -> 1f
            ZoomLevel.Mid -> 3f.coerceIn(zoomMin, zoomMax)
            ZoomLevel.Max -> 5f.coerceAtLeast(zoomMax)
        }
    }
