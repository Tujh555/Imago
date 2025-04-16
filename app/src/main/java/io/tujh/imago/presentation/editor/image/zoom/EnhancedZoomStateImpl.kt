package io.tujh.imago.presentation.editor.image.zoom

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import io.tujh.imago.presentation.editor.image.util.coerceIn
import io.tujh.imago.presentation.editor.image.util.getCropRect
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

open class EnhancedZoomState(
    val imageSize: IntSize,
    initialZoom: Float = 1f,
    minZoom: Float = .5f,
    maxZoom: Float = 5f,
    fling: Boolean = false,
    moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false
) : BaseEnhancedZoomState(
    initialZoom = initialZoom,
    minZoom = minZoom,
    maxZoom = maxZoom,
    fling = fling,
    moveToBounds = moveToBounds,
    zoomable = zoomable,
    pannable = pannable,
    rotatable = rotatable,
    limitPan = limitPan
) {

    private val rectDraw: Rect
        get() = Rect(
            offset = Offset.Zero,
            size = Size(size.width.toFloat(), size.height.toFloat())
        )

    val enhancedZoomData: EnhancedZoomData
        get() = EnhancedZoomData(
            zoom = animatableZoom.targetValue,
            pan = Offset(animatablePanX.targetValue, animatablePanY.targetValue),
            rotation = animatableRotation.targetValue,
            imageRegion = rectDraw,
            visibleRegion = calculateRectBounds()
        )

    private fun calculateRectBounds(): IntRect {
        val width = size.width
        val height = size.height

        val bounds = getBounds()
        val zoom = animatableZoom.targetValue
        val panX = animatablePanX.targetValue.coerceIn(-bounds.x, bounds.x)
        val panY = animatablePanY.targetValue.coerceIn(-bounds.y, bounds.y)

        val horizontalCenterOffset = width * (zoom - 1) / 2f
        val verticalCenterOffset = height * (zoom - 1) / 2f

        val offsetX = (horizontalCenterOffset - panX)
            .coerceAtLeast(0f) / zoom
        val offsetY = (verticalCenterOffset - panY)
            .coerceAtLeast(0f) / zoom

        val offset = Offset(offsetX, offsetY)

        return getCropRect(
            bitmapWidth = imageSize.width,
            bitmapHeight = imageSize.height,
            containerWidth = width.toFloat(),
            containerHeight = height.toFloat(),
            pan = offset,
            zoom = zoom,
            rectSelection = rectDraw
        )
    }
}

open class BaseEnhancedZoomState(
    initialZoom: Float = 1f,
    minZoom: Float = .5f,
    maxZoom: Float = 5f,
    val fling: Boolean = true,
    val moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false
) : ZoomState(
    initialZoom = initialZoom,
    initialRotation = 0f,
    minZoom = minZoom,
    maxZoom = maxZoom,
    zoomable = zoomable,
    pannable = pannable,
    rotatable = rotatable,
    limitPan = limitPan
) {
    private val velocityTracker = VelocityTracker()

    private var doubleTapped = false

    open suspend fun onGesture(
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    ) = coroutineScope {

        doubleTapped = false

        updateZoomState(
            centroid = centroid,
            zoomChange = zoom,
            panChange = pan,
            rotationChange = rotation
        )

        if (fling) {
            if (changes.size == 1) {
                addPosition(mainPointer.uptimeMillis, mainPointer.position)
            }
        }
    }

    open suspend fun onGestureStart() = coroutineScope {}

    open suspend fun onGestureEnd(onBoundsCalculated: () -> Unit) {
        if (!doubleTapped) {

            if (fling && zoom > 1) {
                fling { onBoundsCalculated() }
            } else {
                onBoundsCalculated()
            }

            if (moveToBounds) {
                resetToValidBounds()
            }
        }
    }

    suspend fun onDoubleTap(
        pan: Offset = Offset.Zero,
        zoom: Float = 1f,
        rotation: Float = 0f,
        onAnimationEnd: () -> Unit
    ) {
        doubleTapped = true

        if (fling) {
            resetTracking()
        }
        resetWithAnimation(pan = pan, zoom = zoom, rotation = rotation)
        onAnimationEnd()
    }

    private suspend fun resetToValidBounds() {
        val zoom = zoom.coerceAtLeast(1f)
        val bounds = getBounds()
        val pan = pan.coerceIn(-bounds.x..bounds.x, -bounds.y..bounds.y)
        resetWithAnimation(pan = pan, zoom = zoom, rotation = rotation)
        resetTracking()
    }

    private fun addPosition(timeMillis: Long, position: Offset) {
        velocityTracker.addPosition(
            timeMillis = timeMillis,
            position = position
        )
    }

    private suspend fun fling(onFlingStart: () -> Unit) = coroutineScope {
        val velocityTracker = velocityTracker.calculateVelocity()
        val velocity = Offset(velocityTracker.x, velocityTracker.y)
        var flingStarted = false

        launch {
            animatablePanX.animateDecay(
                velocity.x,
                exponentialDecay(absVelocityThreshold = 20f),
                block = {
                    if (!flingStarted) {
                        onFlingStart()
                        flingStarted = true
                    }
                }
            )
        }

        launch {
            animatablePanY.animateDecay(
                velocity.y,
                exponentialDecay(absVelocityThreshold = 20f),
                block = {
                    if (!flingStarted) {
                        onFlingStart()
                        flingStarted = true
                    }
                }
            )
        }
    }

    private fun resetTracking() {
        velocityTracker.resetTracking()
    }
}
