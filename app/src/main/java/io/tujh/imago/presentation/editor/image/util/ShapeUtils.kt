package io.tujh.imago.presentation.editor.image.util

import android.graphics.Matrix
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.tujh.imago.presentation.editor.image.crop.AspectRatio
import kotlin.math.cos
import kotlin.math.sin

fun createPolygonPath(cx: Float, cy: Float, sides: Int, radius: Float): Path {
    val angle = 2.0 * Math.PI / sides
    return Path().apply {
        moveTo(
            cx + (radius * cos(0.0)).toFloat(),
            cy + (radius * sin(0.0)).toFloat()
        )
        for (i in 1 until sides) {
            lineTo(
                cx + (radius * cos(angle * i)).toFloat(),
                cy + (radius * sin(angle * i)).toFloat()
            )
        }
        close()
    }
}

fun createPolygonShape(sides: Int, degrees: Float = 0f) = GenericShape { size, _ ->
    val radius = size.width.coerceAtMost(size.height) / 2
    addPath(
        createPolygonPath(
            cx = size.width / 2,
            cy = size.height / 2,
            sides = sides,
            radius = radius
        )
    )
    val matrix = Matrix()
    matrix.postRotate(degrees, size.width / 2, size.height / 2)
    asAndroidPath().transform(matrix)
}

fun createRectShape(aspectRatio: AspectRatio) = GenericShape { size, _ ->
    val value = aspectRatio.value
    val width = size.width
    val height = size.height
    val shapeSize =
        if (aspectRatio == AspectRatio.Original) Size(width, height)
        else if (value > 1) Size(width = width, height = width / value)
        else Size(width = height * value, height = height)

    addRect(Rect(offset = Offset.Zero, size = shapeSize))
}

fun Path.scaleAndTranslatePath(
    width: Float,
    height: Float,
) {
    val pathSize = getBounds().size

    val matrix = Matrix()
    matrix.postScale(
        width / pathSize.width,
        height / pathSize.height
    )

    this.asAndroidPath().transform(matrix)

    val left = getBounds().left
    val top = getBounds().top

    translate(Offset(-left, -top))
}

fun buildOutline(
    aspectRatio: AspectRatio,
    coefficient: Float,
    shape: Shape,
    size: Size,
    layoutDirection: LayoutDirection,
    density: Density
): Pair<Offset, Outline> {

    val (shapeSize, offset) = calculateSizeAndOffsetFromAspectRatio(aspectRatio, coefficient, size)

    val outline = shape.createOutline(
        size = shapeSize,
        layoutDirection = layoutDirection,
        density = density
    )
    return Pair(offset, outline)
}

fun calculateSizeAndOffsetFromAspectRatio(
    aspectRatio: AspectRatio,
    coefficient: Float,
    size: Size,
): Pair<Size, Offset> {
    val width = size.width
    val height = size.height

    val value = aspectRatio.value

    val newSize = if (aspectRatio == AspectRatio.Original) {
        Size(width * coefficient, height * coefficient)
    } else if (value > 1) {
        Size(
            width = coefficient * width,
            height = coefficient * width / value
        )
    } else {
        Size(width = coefficient * height * value, height = coefficient * height)
    }

    val left = (width - newSize.width) / 2
    val top = (height - newSize.height) / 2

   return Pair(newSize, Offset(left, top))
}