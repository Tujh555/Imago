package io.tujh.imago.presentation.editor.components.draw.brush

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.asAndroidPath
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class AirbrushBrush(
    private val size: Float = 30f,
    private val brushColor: Color = Color.Green,
    private val opacity: Float = 1f,
    private val density: Float = size / 50,
    startPosition: Offset
) : DrawBrush by DrawBrush(
    startPosition = startPosition,
    initPaint = {
        color = brushColor
        style = PaintingStyle.Fill
        isAntiAlias = true
        blendMode = BlendMode.SrcOver
        alpha = opacity
    }
) {
    override fun move(lastPosition: Offset, currentPosition: Offset) {
        for (i in 0 until (size * density).toInt()) {
            val angle = Math.random() * 2 * PI
            val radius = Math.random() * size / 2
            val x = currentPosition.x + (radius * cos(angle)).toFloat()
            val y = currentPosition.y + (radius * sin(angle)).toFloat()
            path.asAndroidPath().addCircle(x, y, radius.toFloat(), android.graphics.Path.Direction.CW)
        }
    }
}