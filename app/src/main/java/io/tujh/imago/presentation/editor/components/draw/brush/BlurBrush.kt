package io.tujh.imago.presentation.editor.components.draw.brush

import android.graphics.BlurMaskFilter
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle

class BlurBrush(
    private val startPosition: Offset,
    private val size: Float = 20f,
    private val brushColor: Color = Color.Yellow,
    private val blurRadius: Float = 15f,
    private val opacity: Float = 1f
) : PathBrush by PathBrush(
    startPosition = startPosition,
    initPaint = {
        color = brushColor
        style = PaintingStyle.Stroke
        isAntiAlias = true
        blendMode = BlendMode.SrcOver
        strokeWidth = size
        alpha = opacity
        asFrameworkPaint().apply {
            maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
        }
    }
)