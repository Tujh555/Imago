package io.tujh.imago.presentation.editor.image.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.tujh.imago.presentation.editor.image.crop.AspectRatio

fun DrawScope.drawGrid(rect: Rect, strokeWidth: Float, color: Color) {
    val width = rect.width
    val height = rect.height
    val gridWidth = width / 3
    val gridHeight = height / 3

    for (i in 1..2) {
        drawLine(
            color = color,
            start = Offset(rect.left, rect.top + i * gridHeight),
            end = Offset(rect.right, rect.top + i * gridHeight),
            strokeWidth = strokeWidth
        )
    }
    for (i in 1..2) {
        drawLine(
            color,
            start = Offset(rect.left + i * gridWidth, rect.top),
            end = Offset(rect.left + i * gridWidth, rect.bottom),
            strokeWidth = strokeWidth
        )
    }
}

fun DrawScope.drawChecker() {

    val width = this.size.width
    val height = this.size.height

    val checkerWidth = 10.dp.toPx()
    val checkerHeight = 10.dp.toPx()

    val horizontalSteps = (width / checkerWidth).toInt()
    val verticalSteps = (height / checkerHeight).toInt()

    for (y in 0..verticalSteps) {
        for (x in 0..horizontalSteps) {
            val isGrayTile = ((x + y) % 2 == 1)
            drawRect(
                color = if (isGrayTile) Color.LightGray else Color.White,
                topLeft = Offset(x * checkerWidth, y * checkerHeight),
                size = Size(checkerWidth, checkerHeight)
            )
        }
    }
}

fun DrawScope.drawWithLayer(block: DrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}

fun Modifier.drawWithLayer(block: DrawScope.() -> Unit) = this.then(
    Modifier.drawWithContent {
        drawWithLayer {
            block()
        }
    }
)

fun Modifier.drawOutlineWithBlendModeAndChecker(
    aspectRatio: AspectRatio,
    shape: Shape,
    density: Density,
    dstBitmap: ImageBitmap,
    coefficient: Float = .9f,
    color: Color = Color.Red,
) = this.then(
    Modifier.drawWithCache {

        val (offset, outline) = buildOutline(
            aspectRatio,
            coefficient,
            shape,
            size,
            layoutDirection,
            density
        )

        onDrawWithContent {
            drawBlockWithCheckerAndLayer(dstBitmap) {
                translate(left = offset.x, top = offset.y) {
                    drawOutline(
                        outline = outline,
                        color = color,
                    )
                }
            }
        }
    }
)

fun DrawScope.drawBlockWithCheckerAndLayer(
    dstBitmap: ImageBitmap,
    block: DrawScope.() -> Unit,
) {
    drawChecker()
    drawWithLayer {
        val canvasWidth = size.width.toInt()
        val canvasHeight = size.height.toInt()
        block()
        drawImage(
            image = dstBitmap,
            dstSize = IntSize(canvasWidth, canvasHeight),
            blendMode = BlendMode.SrcIn
        )
    }
}
