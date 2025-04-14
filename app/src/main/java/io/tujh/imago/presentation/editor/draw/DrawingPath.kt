package io.tujh.imago.presentation.editor.draw

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path

data class DrawingPath(
    val path: Path,
    val paint: Paint
)

inline fun drawingPath(path: Path = Path(), block: Paint.() -> Unit) = DrawingPath(
    path = path,
    paint = Paint().apply(block)
)