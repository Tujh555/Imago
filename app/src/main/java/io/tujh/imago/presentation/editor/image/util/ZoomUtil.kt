package io.tujh.imago.presentation.editor.image.util

import androidx.compose.ui.graphics.GraphicsLayerScope
import io.tujh.imago.presentation.editor.image.crop.TransformState
import io.tujh.imago.presentation.editor.image.zoom.ZoomLevel
import io.tujh.imago.presentation.editor.image.zoom.ZoomState

fun calculateZoom(
    zoomLevel: ZoomLevel,
    initial: Float,
    min: Float,
    max: Float
): Pair<ZoomLevel, Float> {

    val newZoomLevel: ZoomLevel
    val newZoom: Float

    when (zoomLevel) {
        ZoomLevel.Mid -> {
            newZoomLevel = ZoomLevel.Max
            newZoom = max.coerceAtMost(3f)
        }
        ZoomLevel.Max -> {
            newZoomLevel = ZoomLevel.Min
            newZoom = if (min == initial) initial else min
        }
        else -> {
            newZoomLevel = ZoomLevel.Mid
            newZoom = if (min == initial) (min + max.coerceAtMost(3f)) / 2 else initial
        }
    }
    return Pair(newZoomLevel, newZoom)
}

fun getNextZoomLevel(zoomLevel: ZoomLevel): ZoomLevel = when (zoomLevel) {
    ZoomLevel.Mid -> {
        ZoomLevel.Max
    }
    ZoomLevel.Max -> {
        ZoomLevel.Min
    }
    else -> {
        ZoomLevel.Mid
    }
}

fun GraphicsLayerScope.update(zoomState: ZoomState) {
    val zoom = zoomState.zoom
    this.scaleX = zoom
    this.scaleY = zoom
    val pan = zoomState.pan
    val translationX = pan.x
    val translationY = pan.y
    this.translationX = translationX
    this.translationY = translationY
    this.rotationZ = zoomState.rotation
}

fun GraphicsLayerScope.update(transformState: TransformState) {
    val zoom = transformState.zoom
    this.scaleX = zoom
    this.scaleY = zoom
    val pan = transformState.pan
    val translationX = pan.x
    val translationY = pan.y
    this.translationX = translationX
    this.translationY = translationY
    this.rotationZ = transformState.rotation
}