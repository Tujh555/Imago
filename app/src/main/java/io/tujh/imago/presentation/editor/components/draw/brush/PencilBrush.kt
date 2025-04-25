package io.tujh.imago.presentation.editor.components.draw.brush

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import kotlin.random.Random.Default.nextFloat

class PencilBrush(
    startPosition: Offset,
    private val width: Float,
    private val brushColor: Color,
    private val opacity: Float,
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
) {
    private val jitter: Float = 0.75f

    override fun move(lastPosition: Offset, currentPosition: Offset) {
        val x = currentPosition.x + nextFloat() * width * jitter - width * jitter / 2
        val y = currentPosition.y + nextFloat() * width * jitter - width * jitter / 2
        path.quadraticTo(
            x1 = lastPosition.x,
            y1 = lastPosition.y,
            x2 = (lastPosition.x + x) / 2,
            y2 = (lastPosition.y + y) / 2
        )
    }
}