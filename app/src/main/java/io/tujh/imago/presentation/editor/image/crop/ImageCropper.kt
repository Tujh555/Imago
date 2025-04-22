package io.tujh.imago.presentation.editor.image.crop

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.cropper.crop
import io.tujh.imago.presentation.editor.image.ImageWithConstraints
import io.tujh.imago.presentation.editor.image.getScaledImageBitmap

@Composable
fun ImageCropper(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    cropStyle: CropStyle = CropDefaults.style(),
    cropProperties: CropProperties,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    backgroundColor: Color = Color.Black,
    onCropStateChanged: (CropState) -> Unit,
    onImageScaled: (ImageBitmap) -> Unit,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)? = null,
) {
    ImageWithConstraints(
        modifier = modifier.clipToBounds(),
        contentScale = cropProperties.contentScale,
        filterQuality = filterQuality,
        imageBitmap = imageBitmap,
        drawImage = false
    ) {
        val scaledImageBitmap = getScaledImageBitmap(imageBitmap, cropProperties.contentScale)
        LaunchedEffect(scaledImageBitmap) { onImageScaled(scaledImageBitmap) }
        val containerWidthPx = constraints.maxWidth
        val containerHeightPx = constraints.maxHeight

        val containerWidth: Dp
        val containerHeight: Dp
        val bitmapWidth = scaledImageBitmap.width
        val bitmapHeight = scaledImageBitmap.height
        val imageWidthPx: Int
        val imageHeightPx: Int

        with(LocalDensity.current) {
            imageWidthPx = imageWidth.roundToPx()
            imageHeightPx = imageHeight.roundToPx()
            containerWidth = containerWidthPx.toDp()
            containerHeight = containerHeightPx.toDp()
        }

        val cropType = cropProperties.cropType
        val contentScale = cropProperties.contentScale
        val fixedAspectRatio = cropProperties.fixedAspectRatio
        val cropOutline = cropProperties.cropOutlineProperty.cropOutline
        val resetKeys = getResetKeys(
            scaledImageBitmap,
            imageWidthPx,
            imageHeightPx,
            contentScale,
            cropType,
            fixedAspectRatio
        )

        val cropState = rememberCropState(
            imageSize = IntSize(bitmapWidth, bitmapHeight),
            containerSize = IntSize(containerWidthPx, containerHeightPx),
            drawAreaSize = IntSize(imageWidthPx, imageHeightPx),
            cropProperties = cropProperties,
            keys = resetKeys
        )

        LaunchedEffect(cropState) { onCropStateChanged(cropState) }

        val isHandleTouched by remember(cropState) {
            derivedStateOf {
                cropState is DynamicCropState && handlesTouched(cropState.touchRegion)
            }
        }

        val pressedStateColor = remember(cropStyle.backgroundColor) {
            cropStyle.backgroundColor.copy(cropStyle.backgroundColor.alpha * .7f)
        }

        val transparentColor by animateColorAsState(
            animationSpec = tween(300, easing = LinearEasing),
            targetValue = if (isHandleTouched) pressedStateColor else cropStyle.backgroundColor
        )

        val imageModifier = Modifier
            .size(containerWidth, containerHeight)
            .crop(keys = resetKeys, cropState = cropState)

        LaunchedEffect(key1 = cropProperties) {
            cropState.updateProperties(cropProperties)
        }

        ImageCropper(
            modifier = imageModifier,
            imageBitmap = imageBitmap,
            containerWidth = containerWidth,
            containerHeight = containerHeight,
            imageWidthPx = imageWidthPx,
            imageHeightPx = imageHeightPx,
            handleSize = cropProperties.handleSize,
            overlayRect = cropState.overlayRect,
            cropType = cropType,
            cropOutline = cropOutline,
            cropStyle = cropStyle,
            transparentColor = transparentColor,
            backgroundColor = backgroundColor,
            onDrawGrid = onDrawGrid,
        )
    }
}

@Composable
private fun ImageCropper(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    containerWidth: Dp,
    containerHeight: Dp,
    imageWidthPx: Int,
    imageHeightPx: Int,
    handleSize: Float,
    cropType: CropType,
    cropOutline: CropOutline,
    cropStyle: CropStyle,
    overlayRect: Rect,
    transparentColor: Color,
    backgroundColor: Color,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)?,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ImageCropperImpl(
            modifier = modifier,
            imageBitmap = imageBitmap,
            containerWidth = containerWidth,
            containerHeight = containerHeight,
            imageWidthPx = imageWidthPx,
            imageHeightPx = imageHeightPx,
            cropType = cropType,
            cropOutline = cropOutline,
            handleSize = handleSize,
            cropStyle = cropStyle,
            rectOverlay = overlayRect,
            transparentColor = transparentColor,
            onDrawGrid = onDrawGrid,
        )
    }
}

@Composable
private fun ImageCropperImpl(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    containerWidth: Dp,
    containerHeight: Dp,
    imageWidthPx: Int,
    imageHeightPx: Int,
    cropType: CropType,
    cropOutline: CropOutline,
    handleSize: Float,
    cropStyle: CropStyle,
    transparentColor: Color,
    rectOverlay: Rect,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)?,
) {

    Box(contentAlignment = Alignment.Center) {
        ImageDrawCanvas(
            modifier = modifier,
            imageBitmap = imageBitmap,
            imageWidth = imageWidthPx,
            imageHeight = imageHeightPx
        )

        val drawOverlay = cropStyle.drawOverlay

        val drawGrid = cropStyle.drawGrid
        val overlayColor = cropStyle.overlayColor
        val handleColor = cropStyle.handleColor
        val drawHandles = cropType == CropType.Dynamic
        val strokeWidth = cropStyle.strokeWidth

        DrawingOverlay(
            modifier = Modifier.size(containerWidth, containerHeight),
            drawOverlay = drawOverlay,
            rect = rectOverlay,
            cropOutline = cropOutline,
            drawGrid = drawGrid,
            overlayColor = overlayColor,
            handleColor = handleColor,
            strokeWidth = strokeWidth,
            drawHandles = drawHandles,
            handleSize = handleSize,
            transparentColor = transparentColor,
            onDrawGrid = onDrawGrid,
        )
    }
}

@Composable
private fun getResetKeys(
    scaledImageBitmap: ImageBitmap,
    imageWidthPx: Int,
    imageHeightPx: Int,
    contentScale: ContentScale,
    cropType: CropType,
    fixedAspectRatio: Boolean,
) = remember(
    scaledImageBitmap,
    imageWidthPx,
    imageHeightPx,
    contentScale,
    cropType,
    fixedAspectRatio,
) {
    arrayOf(
        scaledImageBitmap,
        imageWidthPx,
        imageHeightPx,
        contentScale,
        cropType,
        fixedAspectRatio,
    )
}