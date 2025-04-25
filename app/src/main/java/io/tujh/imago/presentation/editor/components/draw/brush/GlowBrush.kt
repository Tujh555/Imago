package io.tujh.imago.presentation.editor.components.draw.brush

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

private const val phase: Float = 0.9f

class GlowBrush(
    startPosition: Offset,
    private val width: Float,
    private val glowColor: Color,
) : DrawBrush by DrawBrush(
    startPosition = startPosition,
    initPaint = {
        style = PaintingStyle.Stroke
        strokeWidth = width
        strokeCap = StrokeCap.Round
        color = Color.Transparent
        asFrameworkPaint()
            .setShadowLayer(30f * phase, 0f, 0f, glowColor.copy(alpha = phase).toArgb())
    }
) {
    context(DrawScope)
    override fun draw(canvas: Canvas) {
        canvas.drawPath(path, paint)
        drawPath(
            color = Color.White.copy((0.4f + phase).coerceAtMost(1f)),
            path = path,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}