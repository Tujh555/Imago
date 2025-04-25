package io.tujh.imago.presentation.editor.components.draw.brush

import android.graphics.BlurMaskFilter
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle

class BlurBrush(
    private val startPosition: Offset,
    private val size: Float,
    private val brushColor: Color,
) : DrawBrush by DrawBrush(
    startPosition = startPosition,
    initPaint = {
        color = brushColor
        style = PaintingStyle.Stroke
        isAntiAlias = true
        blendMode = BlendMode.SrcOver
        strokeWidth = size
        asFrameworkPaint().apply {
            val blurRadius = size * 0.5f
            maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
        }
    }
)