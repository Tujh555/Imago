package io.tujh.imago.presentation.editor.image.zoom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize

@Composable
fun rememberAnimatedZoomState(
    contentSize: DpSize = DpSize.Zero,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    fling: Boolean = true,
    moveToBounds: Boolean = false,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = true,
    key1: Any? = Unit
): AnimatedZoomState {

    val density = LocalDensity.current

    return remember(key1) {
        val size = if (contentSize == DpSize.Zero) {
            IntSize.Zero
        } else density.run {
            IntSize(
                contentSize.width.roundToPx(),
                contentSize.height.roundToPx()
            )
        }

        AnimatedZoomState(
            contentSize = size,
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
fun rememberAnimatedZoomState(
    contentSize: DpSize = DpSize.Zero,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    fling: Boolean = true,
    moveToBounds: Boolean = false,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = true,
    key1: Any?,
    key2: Any?,
): AnimatedZoomState {

    val density = LocalDensity.current

    return remember(key1, key2) {

        val size = if (contentSize == DpSize.Zero) {
            IntSize.Zero
        } else density.run {
            IntSize(
                contentSize.width.roundToPx(),
                contentSize.height.roundToPx()
            )
        }

        AnimatedZoomState(
            contentSize = size,
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
fun rememberAnimatedZoomState(
    contentSize: DpSize = DpSize.Zero,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    fling: Boolean = true,
    moveToBounds: Boolean = false,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = true,
    vararg keys: Any?
): AnimatedZoomState {

    val density = LocalDensity.current

    return remember(*keys) {

        val size = if (contentSize == DpSize.Zero) {
            IntSize.Zero
        } else density.run {
            IntSize(
                contentSize.width.roundToPx(),
                contentSize.height.roundToPx()
            )
        }

        AnimatedZoomState(
            contentSize = size,
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
