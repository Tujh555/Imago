package io.tujh.imago.presentation.editor.image.transform

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import io.tujh.imago.presentation.editor.image.transform.HandlePlacement
import io.tujh.imago.presentation.editor.image.util.drawBorderCircle

private val intervals = floatArrayOf(20f, 20f)

@Composable
fun HandleOverlay(
    modifier: Modifier,
    radius: Float,
    rectDraw: Rect,
    handlePlacement: HandlePlacement = HandlePlacement.Corner
) {
    val transition: InfiniteTransition = rememberInfiniteTransition()
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val pathEffect = PathEffect.dashPathEffect(
        intervals = intervals,
        phase = phase
    )

    val rect = Rect(
        topLeft = Offset(
            rectDraw.topLeft.x + radius,
            rectDraw.topLeft.y + radius
        ),
        bottomRight = Offset(
            rectDraw.bottomRight.x - radius,
            rectDraw.bottomRight.y - radius
        )
    )

    Canvas(modifier = modifier) {
        drawRect(
            topLeft = rect.topLeft,
            size = rect.size,
            color = Color.White,
            style = Stroke(
                width = 2.dp.toPx()
            )
        )
        drawRect(
            topLeft = rect.topLeft,
            size = rect.size,
            color = Color.Black,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = pathEffect
            )
        )

        if (handlePlacement == HandlePlacement.Corner || handlePlacement == HandlePlacement.Both) {
            drawBorderCircle(
                radius = radius,
                center = rect.topLeft
            )
            drawBorderCircle(
                radius = radius,
                center = rect.topRight
            )
            drawBorderCircle(
                radius = radius,
                center = rect.bottomLeft
            )
            drawBorderCircle(
                radius = radius,
                center = rect.bottomRight
            )
        }

        if (handlePlacement == HandlePlacement.Side || handlePlacement == HandlePlacement.Both) {
            drawBorderCircle(
                radius = radius,
                center = rect.centerLeft
            )
            drawBorderCircle(
                radius = radius,
                center = rect.topCenter
            )
            drawBorderCircle(
                radius = radius,
                center = rect.centerRight
            )
            drawBorderCircle(
                radius = radius,
                center = rect.bottomCenter
            )
        }
    }
}
