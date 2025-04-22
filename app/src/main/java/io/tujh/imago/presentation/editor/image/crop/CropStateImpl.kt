package io.tujh.imago.presentation.editor.image.crop

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.IntSize

const val MIN_OVERLAY_RATIO = 0.5F

val CropState.cropData: CropData
    get() = CropData(
        zoom = animatableZoom.targetValue,
        pan = Offset(animatablePanX.targetValue, animatablePanY.targetValue),
        rotation = animatableRotation.targetValue,
        overlayRect = overlayRect,
        cropRect = cropRect
    )

abstract class CropState(
    imageSize: IntSize,
    containerSize: IntSize,
    drawAreaSize: IntSize,
    maxZoom: Float,
    var fling: Boolean = true,
    var aspectRatio: AspectRatio,
    var overlayRatio: Float,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false
) : TransformState(
    imageSize = imageSize,
    containerSize = containerSize,
    drawAreaSize = drawAreaSize,
    initialZoom = 1f,
    initialRotation = 0f,
    maxZoom = maxZoom,
    zoomable = zoomable,
    pannable = pannable,
    rotatable = rotatable,
    limitPan = limitPan
) {

    private val animatableRectOverlay = Animatable(
        getOverlayFromAspectRatio(
            containerSize.width.toFloat(),
            containerSize.height.toFloat(),
            drawAreaSize.width.toFloat(),
            aspectRatio,
            overlayRatio
        ),
        Rect.VectorConverter
    )

    val overlayRect: Rect
        get() = animatableRectOverlay.value

    var cropRect: Rect = Rect.Zero
        get() = getCropRectangle(
            imageSize.width,
            imageSize.height,
            drawAreaRect,
            animatableRectOverlay.targetValue
        )
        private set


    private var initialized: Boolean = false

    var touchRegion by mutableStateOf(TouchRegion.None)

    suspend fun init() {
        animateTransformationToOverlayBounds(overlayRect, animate = true)
        initialized = true
    }

    open suspend fun updateProperties(
        cropProperties: CropProperties,
        forceUpdate: Boolean = false
    ) {

        if (!initialized) return

        fling = cropProperties.fling
        pannable = cropProperties.pannable
        zoomable = cropProperties.zoomable
        rotatable = cropProperties.rotatable

        val maxZoom = cropProperties.maxZoom
        val aspectRatio = cropProperties.aspectRatio
        val overlayRatio = cropProperties.overlayRatio

        if (
            this.aspectRatio.value != aspectRatio.value ||
            maxZoom != zoomMax ||
            this.overlayRatio != overlayRatio ||
            forceUpdate
        ) {
            this.aspectRatio = aspectRatio
            this.overlayRatio = overlayRatio

            zoomMax = maxZoom
            animatableZoom.updateBounds(zoomMin, zoomMax)

            val currentZoom = if (zoom > zoomMax) zoomMax else zoom
            snapZoomTo(currentZoom)
            drawAreaRect = updateImageDrawRectFromTransformation()
            animateOverlayRectTo(
                getOverlayFromAspectRatio(
                    containerSize.width.toFloat(),
                    containerSize.height.toFloat(),
                    drawAreaSize.width.toFloat(),
                    aspectRatio,
                    overlayRatio
                )
            )
        }
        animateTransformationToOverlayBounds(overlayRect, animate = true)
    }

    suspend fun animateOverlayRectTo(
        rect: Rect,
        animationSpec: AnimationSpec<Rect> = tween(400)
    ) {
        animatableRectOverlay.animateTo(
            targetValue = rect,
            animationSpec = animationSpec
        )
    }

    suspend fun snapOverlayRectTo(rect: Rect) {
        animatableRectOverlay.snapTo(rect)
    }

    abstract suspend fun onDown(change: PointerInputChange)

    abstract suspend fun onMove(changes: List<PointerInputChange>)

    abstract suspend fun onUp(change: PointerInputChange)

    abstract suspend fun onGesture(
        centroid: Offset,
        panChange: Offset,
        zoomChange: Float,
        rotationChange: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    )

    abstract suspend fun onGestureStart()

    abstract suspend fun onGestureEnd(onBoundsCalculated: () -> Unit)

    abstract suspend fun onDoubleTap(
        offset: Offset,
        zoom: Float = 1f,
        onAnimationEnd: () -> Unit
    )

    fun isOverlayInImageDrawBounds() =
        drawAreaRect.left <= overlayRect.left &&
                drawAreaRect.top <= overlayRect.top &&
                drawAreaRect.right >= overlayRect.right &&
                drawAreaRect.bottom >= overlayRect.bottom

    fun isRectInContainerBounds(rect: Rect) =
        rect.left >= 0 &&
                rect.right <= containerSize.width &&
                rect.top >= 0 &&
                rect.bottom <= containerSize.height

    fun updateImageDrawRectFromTransformation(): Rect {
        val containerWidth = containerSize.width
        val containerHeight = containerSize.height

        val originalDrawWidth = drawAreaSize.width
        val originalDrawHeight = drawAreaSize.height

        val panX = animatablePanX.targetValue
        val panY = animatablePanY.targetValue

        val left = (containerWidth - originalDrawWidth) / 2
        val top = (containerHeight - originalDrawHeight) / 2

        val zoom = animatableZoom.targetValue

        val newWidth = originalDrawWidth * zoom
        val newHeight = originalDrawHeight * zoom

        return Rect(
            offset = Offset(
                left - (newWidth - originalDrawWidth) / 2 + panX,
                top - (newHeight - originalDrawHeight) / 2 + panY,
            ),
            size = Size(newWidth, newHeight)
        )
    }

    suspend fun animateTransformationToOverlayBounds(
        overlayRect: Rect,
        animate: Boolean,
        animationSpec: AnimationSpec<Float> = tween(400)
    ) {
        val zoom = zoom.coerceAtLeast(MIN_OVERLAY_RATIO)

        val newDrawAreaRect = calculateValidImageDrawRect(overlayRect, drawAreaRect)

        val newZoom =
            calculateNewZoom(oldRect = drawAreaRect, newRect = newDrawAreaRect, zoom = zoom)

        val leftChange = newDrawAreaRect.left - drawAreaRect.left
        val topChange = newDrawAreaRect.top - drawAreaRect.top

        val widthChange = newDrawAreaRect.width - drawAreaRect.width
        val heightChange = newDrawAreaRect.height - drawAreaRect.height

        val panXChange = leftChange + widthChange / 2
        val panYChange = topChange + heightChange / 2

        val newPanX = pan.x + panXChange
        val newPanY = pan.y + panYChange

        if (animate) {
            resetWithAnimation(
                pan = Offset(newPanX, newPanY),
                zoom = newZoom,
                rotation = this.rotation,
                animationSpec = animationSpec
            )
        } else {
            snapPanXto(newPanX)
            snapPanYto(newPanY)
            snapZoomTo(newZoom)
            snapRotationTo(this.rotation)
        }

        resetTracking()

        drawAreaRect = updateImageDrawRectFromTransformation()
    }

    private fun calculateNewZoom(oldRect: Rect, newRect: Rect, zoom: Float): Float {

        if (oldRect.size == Size.Zero || newRect.size == Size.Zero) return zoom

        val widthChange = (newRect.width / oldRect.width)
            .coerceAtLeast(1f)
        val heightChange = (newRect.height / oldRect.height)
            .coerceAtLeast(1f)

        return widthChange.coerceAtLeast(heightChange) * zoom
    }

    private fun calculateValidImageDrawRect(rectOverlay: Rect, rectDrawArea: Rect): Rect {

        var width = rectDrawArea.width
        var height = rectDrawArea.height

        if (width < rectOverlay.width) {
            width = rectOverlay.width
        }

        if (height < rectOverlay.height) {
            height = rectOverlay.height
        }

        var rectImageArea = Rect(offset = rectDrawArea.topLeft, size = Size(width, height))

        if (rectImageArea.left > rectOverlay.left) {
            rectImageArea = rectImageArea.translate(rectOverlay.left - rectImageArea.left, 0f)
        }

        if (rectImageArea.right < rectOverlay.right) {
            rectImageArea = rectImageArea.translate(rectOverlay.right - rectImageArea.right, 0f)
        }

        if (rectImageArea.top > rectOverlay.top) {
            rectImageArea = rectImageArea.translate(0f, rectOverlay.top - rectImageArea.top)
        }

        if (rectImageArea.bottom < rectOverlay.bottom) {
            rectImageArea = rectImageArea.translate(0f, rectOverlay.bottom - rectImageArea.bottom)
        }

        return rectImageArea
    }

    fun getOverlayFromAspectRatio(
        containerWidth: Float,
        containerHeight: Float,
        drawAreaWidth: Float,
        aspectRatio: AspectRatio,
        coefficient: Float
    ): Rect {

        if (aspectRatio == AspectRatio.Original) {
            val imageAspectRatio = imageSize.width.toFloat() / imageSize.height.toFloat()

            val overlayWidthMax = drawAreaWidth.coerceAtMost(containerWidth * coefficient)
            val overlayHeightMax =
                (overlayWidthMax / imageAspectRatio).coerceAtMost(containerHeight * coefficient)

            val offsetX = (containerWidth - overlayWidthMax) / 2f
            val offsetY = (containerHeight - overlayHeightMax) / 2f

            return Rect(
                offset = Offset(offsetX, offsetY),
                size = Size(overlayWidthMax, overlayHeightMax)
            )
        }

        val overlayWidthMax = containerWidth * coefficient
        val overlayHeightMax = containerHeight * coefficient

        val aspectRatioValue = aspectRatio.value

        var width = overlayWidthMax
        var height = overlayWidthMax / aspectRatioValue

        if (height > overlayHeightMax) {
            height = overlayHeightMax
            width = height * aspectRatioValue
        }

        val offsetX = (containerWidth - width) / 2f
        val offsetY = (containerHeight - height) / 2f

        return Rect(offset = Offset(offsetX, offsetY), size = Size(width, height))
    }

    private fun getCropRectangle(
        bitmapWidth: Int,
        bitmapHeight: Int,
        drawAreaRect: Rect,
        overlayRect: Rect
    ): Rect {

        if (drawAreaRect == Rect.Zero || overlayRect == Rect.Zero) return Rect(
            offset = Offset.Zero,
            Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())
        )

        val newRect = calculateValidImageDrawRect(overlayRect, drawAreaRect)

        val overlayWidth = overlayRect.width
        val overlayHeight = overlayRect.height

        val drawAreaWidth = newRect.width
        val drawAreaHeight = newRect.height

        val widthRatio = overlayWidth / drawAreaWidth
        val heightRatio = overlayHeight / drawAreaHeight

        val diffLeft = overlayRect.left - newRect.left
        val diffTop = overlayRect.top - newRect.top

        val croppedBitmapLeft = (diffLeft * (bitmapWidth / drawAreaWidth))
        val croppedBitmapTop = (diffTop * (bitmapHeight / drawAreaHeight))

        val croppedBitmapWidth = bitmapWidth * widthRatio
        val croppedBitmapHeight = bitmapHeight * heightRatio

        return Rect(
            offset = Offset(croppedBitmapLeft, croppedBitmapTop),
            size = Size(croppedBitmapWidth, croppedBitmapHeight)
        )
    }
}
