package io.tujh.imago.presentation.editor.image.beforeafter

import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.tujh.imago.R

@Composable
fun DefaultOverlay(
    width: Dp,
    height: Dp,
    position: Offset,
    overlayStyle: OverlayStyle
) {

    val verticalThumbMove = overlayStyle.verticalThumbMove
    val dividerColor = overlayStyle.dividerColor
    val dividerWidth = overlayStyle.dividerWidth
    val thumbBackgroundColor = overlayStyle.thumbBackgroundColor
    val thumbTintColor = overlayStyle.thumbTintColor
    val thumbShape = overlayStyle.thumbShape
    val thumbElevation = overlayStyle.thumbElevation
    val thumbResource = overlayStyle.thumbResource
    val thumbSize = overlayStyle.thumbSize
    val thumbPositionPercent = overlayStyle.thumbPositionPercent


    var thumbPosX = position.x
    var thumbPosY = position.y

    val linePosition: Float

    val density = LocalDensity.current

    with(density) {
        val thumbRadius = (thumbSize / 2).toPx()
        val imageWidthInPx = width.toPx()
        val imageHeightInPx = height.toPx()

        val horizontalOffset = imageWidthInPx / 2
        val verticalOffset = imageHeightInPx / 2

        linePosition = thumbPosX.coerceIn(0f, imageWidthInPx)
        thumbPosX -= horizontalOffset

        thumbPosY = if (verticalThumbMove) {
            (thumbPosY - verticalOffset)
                .coerceIn(
                    -verticalOffset + thumbRadius,
                    verticalOffset - thumbRadius
                )
        } else {
            ((imageHeightInPx * thumbPositionPercent / 100f - thumbRadius) - verticalOffset)
        }
    }

    Box(
        modifier = Modifier.size(width, height),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            drawLine(
                dividerColor,
                strokeWidth = dividerWidth.toPx(),
                start = Offset(linePosition, 0f),
                end = Offset(linePosition, size.height)
            )
        }

        Icon(
            painter = painterResource(id = thumbResource),
            contentDescription = null,
            tint = thumbTintColor,
            modifier = Modifier
                .offset { IntOffset(thumbPosX.toInt(), thumbPosY.toInt()) }
                .shadow(thumbElevation, thumbShape)
                .background(thumbBackgroundColor)
                .size(thumbSize)
                .padding(4.dp)
        )
    }
}

@Immutable
class OverlayStyle(
    val dividerColor: Color = Color.White,
    val dividerWidth: Dp = 1.5.dp,
    val verticalThumbMove: Boolean = false,
    val thumbBackgroundColor: Color = Color.White,
    val thumbTintColor: Color = Color.Gray,
    val thumbShape: Shape = CircleShape,
    val thumbElevation: Dp = 2.dp,
    @DrawableRes val thumbResource: Int = R.drawable.baseline_swap_horiz_24,
    val thumbSize: Dp = 36.dp,
    @FloatRange(from = 0.0, to = 100.0) val thumbPositionPercent: Float = 85f,
)
