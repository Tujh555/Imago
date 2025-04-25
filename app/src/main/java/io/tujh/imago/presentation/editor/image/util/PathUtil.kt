package io.tujh.imago.presentation.editor.image.util

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath

inline fun path(block: Path.() -> Unit) = Path().apply(block)

inline fun paint(block: Paint.() -> Unit) = Paint().apply(block)

infix fun Path.intersectsWith(other: Path) =
    asAndroidPath().op(other.asAndroidPath(), android.graphics.Path.Op.INTERSECT)