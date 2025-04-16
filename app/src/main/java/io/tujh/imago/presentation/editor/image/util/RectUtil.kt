package io.tujh.imago.presentation.editor.image.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize

fun getCropRect(
    bitmapWidth: Int,
    bitmapHeight: Int,
    containerWidth: Float,
    containerHeight: Float,
    pan: Offset,
    zoom: Float,
    rectSelection: Rect
): IntRect {
    val widthRatio = bitmapWidth / containerWidth
    val heightRatio = bitmapHeight / containerHeight

    val width = (widthRatio * rectSelection.width / zoom).coerceIn(0f, bitmapWidth.toFloat())
    val height = (heightRatio * rectSelection.height / zoom).coerceIn(0f, bitmapHeight.toFloat())

    val offsetXInBitmap = (widthRatio * (pan.x + rectSelection.left / zoom))
        .coerceIn(0f, bitmapWidth - width).toInt()
    val offsetYInBitmap = (heightRatio * (pan.y + rectSelection.top / zoom))
        .coerceIn(0f, bitmapHeight - height).toInt()

    return IntRect(
        offset = IntOffset(offsetXInBitmap, offsetYInBitmap),
        size = IntSize(width.toInt(), height.toInt())
    )
}