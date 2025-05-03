package io.tujh.imago.presentation.editor.image.beforeafter

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.*
import com.smarttoolfactory.gesture.detectMotionEvents
import com.smarttoolfactory.gesture.detectTransformGestures
import io.tujh.imago.presentation.editor.image.util.getParentSize
import io.tujh.imago.presentation.editor.image.util.getScaledBitmapRect
import io.tujh.imago.presentation.editor.image.util.scale
import io.tujh.imago.presentation.editor.image.util.update
import io.tujh.imago.presentation.editor.image.zoom.rememberZoomState
import kotlinx.coroutines.launch

@Composable
fun BeforeAfterImageImpl(
    modifier: Modifier = Modifier,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    @FloatRange(from = 0.0, to = 100.0) progress: Float = 50f,
    onProgressChange: ((Float) -> Unit)? = null,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
    overlay: @Composable BeforeAfterImageScope.() -> Unit = {}
) {
    val semantics = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    } else {
        Modifier
    }

    BoxWithConstraints(
        modifier = modifier.then(semantics),
        contentAlignment = alignment,
    ) {

        val bitmapWidth = beforeImage.width
        val bitmapHeight = beforeImage.height

        val parenSize = getParentSize(bitmapWidth, bitmapHeight)
        val boxWidth: Float = parenSize.width.toFloat()
        val boxHeight: Float = parenSize.height.toFloat()
        val srcSize = Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())
        val dstSize = Size(boxWidth, boxHeight)

        val scaleFactor = contentScale.computeScaleFactor(srcSize, dstSize)
        val imageWidth = bitmapWidth * scaleFactor.scaleX
        val imageHeight = bitmapHeight * scaleFactor.scaleY
        val canvasWidthInDp: Dp
        val canvasHeightInDp: Dp

        with(LocalDensity.current) {
            canvasWidthInDp = imageWidth.coerceAtMost(boxWidth).toDp()
            canvasHeightInDp = imageHeight.coerceAtMost(boxHeight).toDp()
        }

        val bitmapRect = getScaledBitmapRect(
            boxWidth = boxWidth.toInt(),
            boxHeight = boxHeight.toInt(),
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            bitmapWidth = bitmapWidth,
            bitmapHeight = bitmapHeight
        )
        fun scaleToUserValue(offset: Float) =
            scale(0f, boxWidth, offset, 0f, 100f)
        fun scaleToOffset(userValue: Float) =
            scale(0f, 100f, userValue, 0f, boxWidth)

        var rawOffset by remember {
            mutableStateOf(
                Offset(
                    x = scaleToOffset(progress),
                    y = imageHeight.coerceAtMost(boxHeight) / 2f,
                )
            )
        }

        rawOffset = rawOffset.copy(x = scaleToOffset(progress))

        var isHandleTouched by remember { mutableStateOf(false) }

        val zoomState = rememberZoomState(limitPan = true)
        val coroutineScope = rememberCoroutineScope()

        val transformModifier = Modifier.pointerInput(Unit) {
            zoomState.size = this.size
            detectTransformGestures(
                onGesture = { centroid: Offset, panChange: Offset, zoomChange: Float, _, _, _ ->

                    coroutineScope.launch {
                        zoomState.updateZoomState(
                            centroid = centroid,
                            panChange = panChange,
                            zoomChange = zoomChange
                        )
                    }
                }
            )
        }

        val touchModifier = Modifier.pointerInput(Unit) {
            zoomState.size = this.size
            detectMotionEvents(
                onDown = {
                    val position = it.position
                    val xPos = position.x

                    isHandleTouched =
                        ((rawOffset.x - xPos) * (rawOffset.x - xPos) < 5000)
                },
                onMove = {
                    if (isHandleTouched) {
                        rawOffset = it.position
                        onProgressChange?.invoke(
                            scaleToUserValue(rawOffset.x)
                        )
                        it.consume()
                    }
                },
                onUp = {
                    isHandleTouched = false
                }
            )
        }

        val tapModifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomState.resetWithAnimation()
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(zoomState)
        }

        val imageModifier = Modifier
            .clipToBounds()
            .then(if (enableZoom) transformModifier.then(tapModifier) else Modifier)
            .then(if (enableProgressWithTouch) touchModifier else Modifier)
            .then(graphicsModifier)

        ImageLayout(
            modifier = imageModifier,
            constraints = constraints,
            beforeImage = beforeImage,
            afterImage = afterImage,
            position = rawOffset,
            translateX = zoomState.pan.x,
            zoom = zoomState.zoom,
            bitmapRect = bitmapRect,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            canvasWidthInDp = canvasWidthInDp,
            canvasHeightInDp = canvasHeightInDp,
            contentOrder = contentOrder,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
            beforeLabel = beforeLabel,
            afterLabel = afterLabel,
            overlay = overlay
        )
    }
}

@Composable
private fun ImageLayout(
    modifier: Modifier,
    constraints: Constraints,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    position: Offset,
    translateX: Float,
    zoom: Float,
    bitmapRect: IntRect,
    imageWidth: Float,
    imageHeight: Float,
    canvasWidthInDp: Dp,
    canvasHeightInDp: Dp,
    contentOrder: ContentOrder,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    beforeLabel: @Composable BoxScope.() -> Unit,
    afterLabel: @Composable BoxScope.() -> Unit,
    overlay: @Composable BeforeAfterImageScope.() -> Unit
) {
    val imageScopeImpl = BeforeAfterImageScopeImpl(
        density = LocalDensity.current,
        constraints = constraints,
        imageWidth = canvasWidthInDp,
        imageHeight = canvasHeightInDp,
        rect = bitmapRect,
        position = position
    )
    ImageImpl(
        modifier = modifier.size(canvasWidthInDp, canvasHeightInDp),
        beforeImage = beforeImage,
        afterImage = afterImage,
        position = position,
        translateX = translateX,
        zoom = zoom,
        alpha = alpha,
        width = imageWidth.toInt(),
        height = imageHeight.toInt(),
        contentOrder = contentOrder,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel
    )
    imageScopeImpl.overlay()
}

@Composable
private fun ImageImpl(
    modifier: Modifier,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    position: Offset,
    translateX: Float,
    zoom: Float,
    width: Int,
    height: Int,
    contentOrder: ContentOrder,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    beforeLabel: @Composable BoxScope.() -> Unit,
    afterLabel: @Composable BoxScope.() -> Unit,
) {
    val bitmapWidth = beforeImage.width
    val bitmapHeight = beforeImage.height

    Box {

        Canvas(modifier = modifier) {

            val canvasWidth = size.width
            val canvasHeight = size.height
            val touchPosition =
                (width - canvasWidth) / 2f + (position.x / zoom)
                    .coerceIn(0f, canvasWidth)
                    .toInt()
            translate(
                top = (-height + canvasHeight) / 2f,
                left = (-width + canvasWidth) / 2f,
            ) {
                val maxX = (size.width * (zoom - 1) / 2f)
                val pan = (maxX - translateX) / zoom

                val srcOffsetX = ((pan + touchPosition) * bitmapWidth / width).toInt()
                val dstOffsetX = (pan + touchPosition).toInt()

                if (contentOrder == ContentOrder.BeforeAfter) {
                    drawImage(
                        beforeImage,
                        srcSize = IntSize(bitmapWidth, bitmapHeight),
                        dstSize = IntSize(width, height),
                        alpha = alpha,
                        colorFilter = colorFilter,
                        filterQuality = filterQuality
                    )
                    drawImage(
                        afterImage,
                        srcSize = IntSize(bitmapWidth, bitmapHeight),
                        srcOffset = IntOffset(srcOffsetX, 0),
                        dstSize = IntSize(width, height),
                        dstOffset = IntOffset(dstOffsetX, 0),
                        alpha = alpha,
                        colorFilter = colorFilter,
                        filterQuality = filterQuality
                    )
                } else {
                    drawImage(
                        afterImage,
                        srcSize = IntSize(bitmapWidth, bitmapHeight),
                        dstSize = IntSize(width, height),
                        alpha = alpha,
                        colorFilter = colorFilter,
                        filterQuality = filterQuality
                    )
                    drawImage(
                        beforeImage,
                        srcSize = IntSize(bitmapWidth, bitmapHeight),
                        srcOffset = IntOffset(srcOffsetX, 0),
                        dstSize = IntSize(width, height),
                        dstOffset = IntOffset(dstOffsetX, 0),
                        alpha = alpha,
                        colorFilter = colorFilter,
                        filterQuality = filterQuality
                    )
                }
            }
        }

        if (contentOrder == ContentOrder.BeforeAfter) {
            beforeLabel()
            afterLabel()
        } else {
            beforeLabel()
            afterLabel()
        }
    }
}