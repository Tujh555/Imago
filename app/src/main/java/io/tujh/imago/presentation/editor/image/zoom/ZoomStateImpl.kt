package io.tujh.imago.presentation.editor.image.zoom

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Stable
open class ZoomState(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    open val zoomable: Boolean = true,
    open val pannable: Boolean = true,
    open val rotatable: Boolean = true,
    open val limitPan: Boolean = false
) {

    val zoomMin = minZoom.coerceAtLeast(.5f)
    val zoomMax = maxZoom.coerceAtLeast(1f)
    val zoomInitial = initialZoom.coerceIn(zoomMin, zoomMax)
    val rotationInitial = initialRotation % 360

    val animatablePanX = Animatable(0f)
    val animatablePanY = Animatable(0f)
    val animatableZoom = Animatable(zoomInitial)
    val animatableRotation = Animatable(rotationInitial)

    var size: IntSize = IntSize.Zero

    init {
        animatableZoom.updateBounds(zoomMin, zoomMax)
        require(zoomMax >= zoomMin)
    }

    val pan: Offset
        get() = Offset(animatablePanX.value, animatablePanY.value)

    val zoom: Float
        get() = animatableZoom.value

    val rotation: Float
        get() = animatableRotation.value

    val isZooming: Boolean
        get() = animatableZoom.isRunning

    val isPanning: Boolean
        get() = animatablePanX.isRunning || animatablePanY.isRunning

    val isRotating: Boolean
        get() = animatableRotation.isRunning

    val isAnimationRunning: Boolean
        get() = isZooming || isPanning || isRotating

    open fun updateBounds(lowerBound: Offset?, upperBound: Offset?) {
        animatablePanX.updateBounds(lowerBound?.x, upperBound?.x)
        animatablePanY.updateBounds(lowerBound?.y, upperBound?.y)
    }

    open fun getBounds(size: IntSize): Offset {
        val maxX = (size.width * (zoom - 1) / 2f).coerceAtLeast(0f)
        val maxY = (size.height * (zoom - 1) / 2f).coerceAtLeast(0f)
        return Offset(maxX, maxY)
    }

    protected open fun getBounds(): Offset {
        return getBounds(size)
    }

    open suspend fun updateZoomState(
        centroid: Offset,
        panChange: Offset,
        zoomChange: Float,
        rotationChange: Float = 1f,
    ) {
        val newZoom = (this.zoom * zoomChange).coerceIn(zoomMin, zoomMax)

        snapZoomTo(newZoom)
        val newRotation = if (rotatable) {
            this.rotation + rotationChange
        } else {
            0f
        }
        snapRotationTo(newRotation)

        if (pannable) {
            val newPan = this.pan + panChange.times(this.zoom)
            val boundPan = limitPan && !rotatable

            if (boundPan) {
                val bound = getBounds(size)
                updateBounds(bound.times(-1f), bound)
            }
            snapPanXto(newPan.x)
            snapPanYto(newPan.y)
        }
    }

    open suspend fun resetWithAnimation(
        pan: Offset = Offset.Zero,
        zoom: Float = 1f,
        rotation: Float = 0f
    ) = coroutineScope {
        launch { animatePanXto(pan.x) }
        launch { animatePanYto(pan.y) }
        launch { animateZoomTo(zoom) }
        launch { animateRotationTo(rotation) }
    }

    suspend fun animatePanXto(panX: Float) {
        if (pannable && pan.x != panX) {
            animatablePanX.animateTo(panX)
        }
    }

    suspend fun animatePanYto(panY: Float) {
        if (pannable && pan.y != panY) {
            animatablePanY.animateTo(panY)
        }
    }

    suspend fun animateZoomTo(zoom: Float) {
        if (zoomable && this.zoom != zoom) {
            val newZoom = zoom.coerceIn(zoomMin, zoomMax)
            animatableZoom.animateTo(newZoom)
        }
    }

    suspend fun animateRotationTo(rotation: Float) {
        if (rotatable && this.rotation != rotation) {
            animatableRotation.animateTo(rotation)
        }
    }

    suspend fun snapPanXto(panX: Float) {
        if (pannable) {
            animatablePanX.snapTo(panX)
        }
    }

    suspend fun snapPanYto(panY: Float) {
        if (pannable) {
            animatablePanY.snapTo(panY)
        }
    }

    suspend fun snapZoomTo(zoom: Float) {
        if (zoomable) {
            animatableZoom.snapTo(zoom.coerceIn(zoomMin, zoomMax))
        }
    }

    suspend fun snapRotationTo(rotation: Float) {
        if (rotatable) {
            animatableRotation.snapTo(rotation)
        }
    }
}
