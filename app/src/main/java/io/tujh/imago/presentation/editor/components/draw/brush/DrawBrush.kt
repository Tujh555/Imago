package io.tujh.imago.presentation.editor.components.draw.brush

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.tujh.imago.presentation.editor.image.util.paint
import io.tujh.imago.presentation.editor.image.util.path

@Stable
interface DrawBrush {
    val path: Path
    val paint: Paint

    fun move(lastPosition: Offset, currentPosition: Offset)

    fun dismiss(position: Offset)

    context(DrawScope)
    fun draw(canvas: Canvas)
}

inline fun DrawBrush(
    initPath: Path.() -> Unit,
    initPaint: Paint.() -> Unit
): DrawBrush {
    val path = path(initPath)
    val paint = paint(initPaint)

    return object : DrawBrush {
        override val paint = paint
        override val path = path

        override fun move(lastPosition: Offset, currentPosition: Offset) {
            path.quadraticTo(
                x1 = lastPosition.x,
                y1 = lastPosition.y,
                x2 = (lastPosition.x + currentPosition.x) / 2,
                y2 = (lastPosition.y + currentPosition.y) / 2
            )
        }

        override fun dismiss(position: Offset) {
            path.lineTo(position.x, position.y)
        }

        context(DrawScope)
        override fun draw(canvas: Canvas) {
            canvas.drawPath(path, paint)
        }
    }
}

inline fun DrawBrush(startPosition: Offset, initPaint: Paint.() -> Unit) = DrawBrush(
    initPaint = initPaint,
    initPath = { moveTo(startPosition.x, startPosition.y) }
)