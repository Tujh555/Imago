package io.tujh.imago.presentation.editor.components.draw.brush

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap

class MarkerBrush(
    startPosition: Offset,
    private val width: Float,
    private val brushColor: Color,
) : DrawBrush by DrawBrush(
    startPosition = startPosition,
    initPaint = {
        color = brushColor
        strokeWidth = width
        style = PaintingStyle.Stroke
        isAntiAlias = true
        strokeCap = StrokeCap.Square
        blendMode = BlendMode.SrcOver
        alpha = 0.5f
    }
)