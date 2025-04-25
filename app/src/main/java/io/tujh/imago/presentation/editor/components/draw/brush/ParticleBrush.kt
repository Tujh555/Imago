package io.tujh.imago.presentation.editor.components.draw.brush

import android.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val particleSpeed: Float = 15f
private const val particleAngle: Float = 30f

class ParticleBrush(
    startPosition: Offset,
    private val size: Float,
    private val brushColor: Color,
    private val opacity: Float,
) : DrawBrush by DrawBrush(
    startPosition = startPosition,
    initPaint = {
        color = brushColor
        style = PaintingStyle.Fill
        isAntiAlias = true
        strokeCap = StrokeCap.Round
        alpha = opacity
    }
) {
    override fun move(lastPosition: Offset, currentPosition: Offset) {
        for (i in 0 until 20) {
            val angle = Math.toRadians((Random.nextDouble() * 2 - 1) * particleAngle).toFloat()
            val dx = particleSpeed * cos(angle)
            val dy = particleSpeed * sin(angle)
            val particleX = lastPosition.x + dx
            val particleY = lastPosition.y + dy
            path.asAndroidPath().addCircle(particleX, particleY, size / 2, Path.Direction.CW)
        }
    }
}