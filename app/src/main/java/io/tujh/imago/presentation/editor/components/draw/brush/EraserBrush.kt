package io.tujh.imago.presentation.editor.components.draw.brush

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill

class EraserBrush(
    startPosition: Offset,
    private val width: Float
) : DrawBrush {
    private val underlying = DrawBrush(
        startPosition = startPosition,
        initPaint = {
            color = Color.Transparent
            strokeWidth = width * 2
            style = PaintingStyle.Stroke
            isAntiAlias = true
            strokeCap = StrokeCap.Round
            strokeJoin = StrokeJoin.Round
            blendMode = BlendMode.Clear
        }
    )
    override val path = underlying.path
    override val paint = underlying.paint
    private var circlePosition: Offset = Offset.Unspecified

    override fun move(lastPosition: Offset, currentPosition: Offset) {
        underlying.move(lastPosition, currentPosition)
        circlePosition = currentPosition
    }

    override fun dismiss(position: Offset) {
        circlePosition = Offset.Unspecified
    }

    context(DrawScope)
    override fun draw(canvas: Canvas) {
        underlying.draw(canvas)
        if (circlePosition.isValid()) {
            drawCircle(
                color = Color.White,
                radius = width,
                center = circlePosition,
                style = Fill,
                blendMode = BlendMode.SrcOver
            )
        }
    }
}