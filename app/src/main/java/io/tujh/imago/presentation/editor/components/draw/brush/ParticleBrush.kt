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

class ParticleBrush(
    private val size: Float = 8f,
    private val brushColor: Color = Color.Red,
    private val particleDensity: Int = 5,
    private val particleSize: Float = 20f,
    private val particleSpeed: Float = 15f,
    private val particleAngle: Float = 30f,
    startPosition: Offset,
) : PathBrush by PathBrush(
    startPosition = startPosition,
    initPaint = {
        color = brushColor
        style = PaintingStyle.Fill
        isAntiAlias = true
        strokeCap = StrokeCap.Round
    }
) {
    override fun move(lastPosition: Offset, currentPosition: Offset) {
        val numParticles = (size * particleDensity).toInt()
        for (i in 0 until numParticles) {
            val angle = Math.toRadians((Random.nextDouble() * 2 - 1) * particleAngle).toFloat()
            val dx = particleSpeed * cos(angle)
            val dy = particleSpeed * sin(angle)
            val particleX = lastPosition.x + dx
            val particleY = lastPosition.y + dy
            path.asAndroidPath().addCircle(particleX, particleY, particleSize / 2, Path.Direction.CW)
        }
    }
}