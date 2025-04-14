package io.tujh.imago.presentation.editor.image

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import io.tujh.imago.presentation.editor.image.util.getParentSize
import io.tujh.imago.presentation.editor.image.util.getScaledBitmapRect

@Composable
fun ImageWithConstraints(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    drawImage: Boolean = true,
    content: @Composable ImageScope.() -> Unit = {}
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = alignment) {
        val bitmapWidth = imageBitmap.width
        val bitmapHeight = imageBitmap.height

        val (boxWidth: Int, boxHeight: Int) = getParentSize(bitmapWidth, bitmapHeight)

        // Src is Bitmap, Dst is the container(Image) that Bitmap will be displayed
        val srcSize = Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())
        val dstSize = Size(boxWidth.toFloat(), boxHeight.toFloat())

        val scaleFactor = contentScale.computeScaleFactor(srcSize, dstSize)

        // Image is the container for bitmap that is located inside Box
        // image bounds can be smaller or bigger than its parent based on how it's scaled
        val imageWidth = bitmapWidth * scaleFactor.scaleX
        val imageHeight = bitmapHeight * scaleFactor.scaleY

        val bitmapRect = getScaledBitmapRect(
            boxWidth = boxWidth,
            boxHeight = boxHeight,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            bitmapWidth = bitmapWidth,
            bitmapHeight = bitmapHeight
        )

        ImageLayout(
            constraints = constraints,
            imageBitmap = imageBitmap,
            bitmapRect = bitmapRect,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            boxWidth = boxWidth,
            boxHeight = boxHeight,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
            drawImage = drawImage,
            content = content
        )
    }
}

@Composable
private fun ImageLayout(
    constraints: Constraints,
    imageBitmap: ImageBitmap,
    bitmapRect: IntRect,
    imageWidth: Float,
    imageHeight: Float,
    boxWidth: Int,
    boxHeight: Int,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    drawImage: Boolean = true,
    content: @Composable ImageScope.() -> Unit
) {
    val density = LocalDensity.current
    val canvasWidthInDp: Dp
    val canvasHeightInDp: Dp

    with(density) {
        canvasWidthInDp = imageWidth.coerceAtMost(boxWidth.toFloat()).toDp()
        canvasHeightInDp = imageHeight.coerceAtMost(boxHeight.toFloat()).toDp()
    }
    val imageScopeImpl = ImageScopeImpl(
        density = density,
        constraints = constraints,
        imageWidth = canvasWidthInDp,
        imageHeight = canvasHeightInDp,
        rect = bitmapRect
    )

    if (drawImage) {
        ImageImpl(
            modifier = Modifier.size(canvasWidthInDp, canvasHeightInDp),
            imageBitmap = imageBitmap,
            alpha = alpha,
            width = imageWidth.toInt(),
            height = imageHeight.toInt(),
            colorFilter = colorFilter,
            filterQuality = filterQuality
        )
    }

    imageScopeImpl.content()
}

@Composable
private fun ImageImpl(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    width: Int,
    height: Int,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
) {
    val bitmapWidth = imageBitmap.width
    val bitmapHeight = imageBitmap.height

    Canvas(modifier = modifier.clipToBounds()) {
        val canvasWidth = size.width.toInt()
        val canvasHeight = size.height.toInt()
        translate(top = (-height + canvasHeight) / 2f, left = (-width + canvasWidth) / 2f) {
            drawImage(
                imageBitmap,
                srcSize = IntSize(bitmapWidth, bitmapHeight),
                dstSize = IntSize(width, height),
                alpha = alpha,
                colorFilter = colorFilter,
                filterQuality = filterQuality
            )
        }
    }
}