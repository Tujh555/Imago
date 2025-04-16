package io.tujh.imago.presentation.editor.image.zoom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberZoomState(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    key1: Any? = Unit
): ZoomState {
    return remember(key1) {
        ZoomState(
            initialZoom = initialZoom,
            initialRotation = initialRotation,
            minZoom = minZoom,
            maxZoom = maxZoom,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = limitPan
        )
    }
}

@Composable
fun rememberZoomState(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    key1: Any?,
    key2: Any?,
): ZoomState {
    return remember(key1, key2) {
        ZoomState(
            initialZoom = initialZoom,
            initialRotation = initialRotation,
            minZoom = minZoom,
            maxZoom = maxZoom,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = limitPan
        )
    }
}

@Composable
fun rememberZoomState(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    vararg keys: Any?
): ZoomState {
    return remember(*keys) {
        ZoomState(
            initialZoom = initialZoom,
            initialRotation = initialRotation,
            minZoom = minZoom,
            maxZoom = maxZoom,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = limitPan
        )
    }
}