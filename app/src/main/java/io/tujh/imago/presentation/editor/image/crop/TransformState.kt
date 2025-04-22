package io.tujh.imago.presentation.editor.image.crop

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private const val MAX_ZOOM = 10F

@Stable
open class TransformState(
    val imageSize: IntSize,
    val containerSize: IntSize,
    val drawAreaSize: IntSize,
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = MIN_OVERLAY_RATIO,
    maxZoom: Float = MAX_ZOOM,
    var zoomable: Boolean = true,
    var pannable: Boolean = true,
    var rotatable: Boolean = true,
    var limitPan: Boolean = false
) {

    var drawAreaRect: Rect by mutableStateOf(
        Rect(
            offset = Offset(
                x = ((containerSize.width - drawAreaSize.width) / 2).toFloat(),
                y = ((containerSize.height - drawAreaSize.height) / 2).toFloat()
            ),
            size = Size(drawAreaSize.width.toFloat(), drawAreaSize.height.toFloat())
        )
    )

    val zoomMin = minZoom.coerceAtLeast(MIN_OVERLAY_RATIO)
    var zoomMax = maxZoom.coerceAtLeast(1f)
    private val zoomInitial = initialZoom.coerceIn(zoomMin, zoomMax)
    private val rotationInitial = initialRotation % 360

    val animatablePanX = Animatable(0f)
    val animatablePanY = Animatable(0f)
    val animatableZoom = Animatable(zoomInitial)
    val animatableRotation = Animatable(rotationInitial)

    private val velocityTracker = VelocityTracker()

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

    open suspend fun updateTransformState(
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
            snapPanXto(newPan.x)
            snapPanYto(newPan.y)
        }
    }

    suspend fun resetWithAnimation(
        pan: Offset = Offset.Zero,
        zoom: Float = 1f,
        rotation: Float = 0f,
        animationSpec: AnimationSpec<Float> = tween(400)
    ) = coroutineScope {
        launch { animatePanXto(pan.x, animationSpec) }
        launch { animatePanYto(pan.y, animationSpec) }
        launch { animateZoomTo(zoom, animationSpec) }
        launch { animateRotationTo(rotation, animationSpec) }
    }

    suspend fun animatePanXto(
        panX: Float,
        animationSpec: AnimationSpec<Float> = tween(400)
    ) {
        if (pannable && pan.x != panX) {
            animatablePanX.animateTo(panX, animationSpec)
        }
    }

    suspend fun animatePanYto(
        panY: Float,
        animationSpec: AnimationSpec<Float> = tween(400)
    ) {
        if (pannable && pan.y != panY) {
            animatablePanY.animateTo(panY, animationSpec)
        }
    }

    suspend fun animateZoomTo(
        zoom: Float,
        animationSpec: AnimationSpec<Float> = tween(400)
    ) {
        if (zoomable && this.zoom != zoom) {
            val newZoom = zoom.coerceIn(zoomMin, zoomMax)
            animatableZoom.animateTo(newZoom, animationSpec)
        }
    }

    suspend fun animateRotationTo(
        rotation: Float,
        animationSpec: AnimationSpec<Float> = tween(400)
    ) {
        if (rotatable && this.rotation != rotation) {
            animatableRotation.animateTo(rotation, animationSpec)
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

    fun addPosition(timeMillis: Long, position: Offset) {
        velocityTracker.addPosition(
            timeMillis = timeMillis,
            position = position
        )
    }

    suspend fun fling(onFlingStart: () -> Unit) = coroutineScope {
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

    fun resetTracking() {
        velocityTracker.resetTracking()
    }
}
