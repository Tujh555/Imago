package io.tujh.imago.presentation.editor.components.draw.brush

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

class BasicBrush(
    startPosition: Offset,
    width: Float,
    brushColor: Color,
    opacity: Float,
) : DrawBrush by DrawBrush(
    startPosition = startPosition,
    initPaint = {
        color = brushColor
        strokeWidth = width
        style = PaintingStyle.Stroke
        isAntiAlias = true
        strokeCap = StrokeCap.Round
        alpha = opacity
        strokeJoin = StrokeJoin.Round
        blendMode = BlendMode.SrcOver
    }
)