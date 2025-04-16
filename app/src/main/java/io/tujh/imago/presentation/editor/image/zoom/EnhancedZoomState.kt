package io.tujh.imago.presentation.editor.image.zoom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize

@Composable
fun rememberEnhancedZoomState(
    imageSize: IntSize,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    fling: Boolean = false,
    moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    key1: Any? = Unit
): EnhancedZoomState {
    return remember(key1) {
        EnhancedZoomState(
            imageSize = imageSize,
            initialZoom = initialZoom,
            minZoom = minZoom,
            maxZoom = maxZoom,
            fling = fling,
            moveToBounds = moveToBounds,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = limitPan
        )
    }
}

@Composable
fun rememberEnhancedZoomState(
    imageSize: IntSize,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    fling: Boolean = false,
    moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    key1: Any?,
    key2: Any?,
): EnhancedZoomState {
    return remember(key1, key2) {
        EnhancedZoomState(
            imageSize = imageSize,
            initialZoom = initialZoom,
            minZoom = minZoom,
            maxZoom = maxZoom,
            fling = fling,
            moveToBounds = moveToBounds,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = limitPan
        )
    }
}

@Composable
fun rememberEnhancedZoomState(
    imageSize: IntSize,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    fling: Boolean = false,
    moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    vararg keys: Any?
): EnhancedZoomState {
    return remember(*keys) {
        EnhancedZoomState(
            imageSize = imageSize,
            initialZoom = initialZoom,
            minZoom = minZoom,
            maxZoom = maxZoom,
            fling = fling,
            moveToBounds = moveToBounds,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = limitPan
        )
    }
}
