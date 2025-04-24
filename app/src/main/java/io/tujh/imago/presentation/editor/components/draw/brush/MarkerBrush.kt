package io.tujh.imago.presentation.editor.components.draw.brush

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap

class MarkerBrush(
    private val width: Float = 20f,
    private val brushColor: Color = Color.Yellow,
    private val opacity: Float = 0.5f,
    startPosition: Offset,
) : PathBrush by PathBrush(
    startPosition = startPosition,
    initPaint = {
        color = brushColor
        strokeWidth = width
        style = PaintingStyle.Stroke
        isAntiAlias = true
        strokeCap = StrokeCap.Square
        blendMode = BlendMode.SrcOver
        alpha = opacity
    }
)