package io.tujh.imago.presentation.editor.components.draw.brush

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

@Stable
interface DrawBrush {
    fun move(lastPosition: Offset, currentPosition: Offset)

    fun dismiss(position: Offset)

    context(DrawScope)
    fun draw(canvas: Canvas)
}

interface PathBrush : DrawBrush {
    val path: Path
    val paint: Paint
}

inline fun PathBrush(
    initPath: Path.() -> Unit,
    initPaint: Paint.() -> Unit
): PathBrush {
    val path = Path().apply(initPath)
    val paint = Paint().apply(initPaint)

    return object : PathBrush {
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

inline fun PathBrush(startPosition: Offset, initPaint: Paint.() -> Unit) = PathBrush(
    initPaint = initPaint,
    initPath = { moveTo(startPosition.x, startPosition.y) }
)