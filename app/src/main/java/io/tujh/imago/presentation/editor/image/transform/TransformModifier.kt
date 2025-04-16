package io.tujh.imago.presentation.editor.image.transform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChange
import com.smarttoolfactory.gesture.pointerMotionEvents
import io.tujh.imago.presentation.editor.image.util.getDistanceToEdgeFromTouch
import io.tujh.imago.presentation.editor.image.util.getTouchRegion

fun Modifier.transform(
    enabled: Boolean,
    size: Size,
    touchRegionRadius: Float,
    minDimension: Float,
    handlePlacement: HandlePlacement,
    transform: Transform,
    onDown: (Transform, Rect) -> Unit,
    onMove: (Transform, Rect) -> Unit,
    onUp: (Transform, Rect) -> Unit,
) = composed(

    factory = {
        val rectBounds by remember {
            mutableStateOf(Rect(offset = Offset.Zero, size = size))
        }
        var rectDraw by remember {
            mutableStateOf(rectBounds.copy())
        }

        var currentTransform by remember {
            mutableStateOf(transform)
        }
        var rectTemp by remember { mutableStateOf(rectBounds.copy()) }

        var touchRegion by remember(enabled) { mutableStateOf(TouchRegion.None) }
        var touchPositionOnScreen: Offset
        var distanceToEdgeFromTouch = Offset.Zero

        pointerMotionEvents(
            onDown = { change: PointerInputChange ->

                if (enabled) {
                    rectTemp = rectDraw.copy()
                    val position = change.position
                    val touchPositionScreenX =
                        rectDraw.left + position.x * rectDraw.width / rectBounds.width
                    val touchPositionScreenY =
                        rectDraw.top + position.y * rectDraw.height / rectBounds.height

                    touchPositionOnScreen = Offset(touchPositionScreenX, touchPositionScreenY)

                    touchRegion = getTouchRegion(
                        position = touchPositionOnScreen,
                        rect = rectDraw,
                        handlePlacement = handlePlacement,
                        threshold = touchRegionRadius * 2
                    )
                    distanceToEdgeFromTouch =
                        getDistanceToEdgeFromTouch(touchRegion, rectTemp, touchPositionOnScreen)

                    onDown(currentTransform, rectDraw)
                }
            },
            onMove = { change: PointerInputChange ->

                if (!enabled) return@pointerMotionEvents

                val position = change.position
                val screenPositionX =
                    rectDraw.left + position.x * rectDraw.width / rectBounds.width +
                            distanceToEdgeFromTouch.x
                val screenPositionY =
                    rectDraw.top + position.y * rectDraw.height / rectBounds.height +
                            distanceToEdgeFromTouch.y

                when (touchRegion) {
                    TouchRegion.TopLeft -> {
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtMost(rectTemp.right - minDimension),
                            screenPositionY.coerceAtMost(rectTemp.bottom - minDimension)
                        )

                        rectDraw = Rect(
                            left = touchPositionOnScreen.x,
                            top = touchPositionOnScreen.y,
                            right = rectTemp.right,
                            bottom = rectTemp.bottom
                        )
                        val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                        val verticalCenter = (rectDraw.height - rectBounds.height) / 2
                        val widthRatio = rectDraw.width / rectBounds.width
                        val heightRatio = rectDraw.height / rectBounds.height
                        val xTranslation = touchPositionOnScreen.x + horizontalCenter
                        val yTranslation = touchPositionOnScreen.y + verticalCenter

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            translationY = yTranslation,
                            scaleX = widthRatio,
                            scaleY = heightRatio
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.BottomLeft -> {
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtMost(rectTemp.right - minDimension),
                            screenPositionY.coerceAtLeast(rectTemp.top + minDimension)
                        )

                        rectDraw = Rect(
                            left = touchPositionOnScreen.x,
                            top = rectTemp.top,
                            right = rectTemp.right,
                            bottom = touchPositionOnScreen.y,
                        )
                        val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                        val verticalCenter = (rectDraw.height - rectBounds.height) / 2
                        val widthRatio = rectDraw.width / rectBounds.width
                        val heightRatio = rectDraw.height / rectBounds.height
                        val xTranslation = touchPositionOnScreen.x + horizontalCenter
                        val yTranslation = touchPositionOnScreen.y - rectDraw.height + verticalCenter

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            translationY = yTranslation,
                            scaleX = widthRatio,
                            scaleY = heightRatio
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.TopRight -> {
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtLeast(rectTemp.left + minDimension),
                            screenPositionY.coerceAtMost(rectTemp.bottom - minDimension)
                        )

                        rectDraw = Rect(
                            left = rectTemp.left,
                            top = touchPositionOnScreen.y,
                            right = touchPositionOnScreen.x,
                            bottom = rectTemp.bottom,
                        )
                        val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                        val verticalCenter = (rectDraw.height - rectBounds.height) / 2
                        val widthRatio = rectDraw.width / rectBounds.width
                        val heightRatio = rectDraw.height / rectBounds.height
                        val xTranslation = touchPositionOnScreen.x - rectDraw.width + horizontalCenter
                        val yTranslation = touchPositionOnScreen.y + verticalCenter

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            translationY = yTranslation,
                            scaleX = widthRatio,
                            scaleY = heightRatio
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.BottomRight -> {
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtLeast(rectTemp.left + minDimension),
                            screenPositionY.coerceAtLeast(rectTemp.top + minDimension)
                        )

                        rectDraw = Rect(
                            left = rectTemp.left,
                            top = rectTemp.top,
                            right = touchPositionOnScreen.x,
                            bottom = touchPositionOnScreen.y,
                        )
                        val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                        val verticalCenter = (rectDraw.height - rectBounds.height) / 2
                        val widthRatio = rectDraw.width / rectBounds.width
                        val heightRatio = rectDraw.height / rectBounds.height
                        val xTranslation = touchPositionOnScreen.x - rectDraw.width + horizontalCenter
                        val yTranslation = touchPositionOnScreen.y - rectDraw.height + verticalCenter

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            translationY = yTranslation,
                            scaleX = widthRatio,
                            scaleY = heightRatio
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.CenterLeft -> {
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtMost(rectTemp.right - minDimension),
                            screenPositionY.coerceAtMost(rectTemp.bottom - minDimension)
                        )

                        rectDraw = rectDraw.copy(left = touchPositionOnScreen.x)

                        val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                        val widthRatio = rectDraw.width / rectBounds.width
                        val xTranslation = touchPositionOnScreen.x + horizontalCenter

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            scaleX = widthRatio,
                        )
                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.TopCenter -> {
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtMost(rectTemp.right - minDimension),
                            screenPositionY.coerceAtMost(rectTemp.bottom - minDimension)
                        )

                        rectDraw = rectDraw.copy(top = touchPositionOnScreen.y)
                        val verticalCenter = (rectDraw.height - rectBounds.height) / 2
                        val heightRatio = rectDraw.height / rectBounds.height
                        val yTranslation = touchPositionOnScreen.y + verticalCenter

                        currentTransform = currentTransform.copy(
                            translationY = yTranslation,
                            scaleY = heightRatio
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.CenterRight -> {
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtLeast(rectTemp.left + minDimension),
                            screenPositionY.coerceAtLeast(rectTemp.top + minDimension)
                        )

                        rectDraw = rectDraw.copy(right = touchPositionOnScreen.x)
                        val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                        val widthRatio = rectDraw.width / rectBounds.width
                        val xTranslation = touchPositionOnScreen.x - rectDraw.width + horizontalCenter

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            scaleX = widthRatio
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.BottomCenter -> {
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtLeast(rectTemp.left + minDimension),
                            screenPositionY.coerceAtLeast(rectTemp.top + minDimension)
                        )

                        rectDraw = rectDraw.copy(bottom = touchPositionOnScreen.y)
                        val verticalCenter = (rectDraw.height - rectBounds.height) / 2
                        val heightRatio = rectDraw.height / rectBounds.height
                        val yTranslation = touchPositionOnScreen.y - rectDraw.height + verticalCenter

                        currentTransform = currentTransform.copy(
                            translationY = yTranslation,
                            scaleY = heightRatio
                        )
                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.Inside -> {
                        val drag = change.positionChange()

                        val scaledDragX = drag.x * rectDraw.width / rectBounds.width
                        val scaledDragY = drag.y * rectDraw.height / rectBounds.height

                        val xTranslation = currentTransform.translationX + scaledDragX
                        val yTranslation = currentTransform.translationY + scaledDragY

                        rectDraw = rectDraw.translate(scaledDragX, scaledDragY)

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            translationY = yTranslation
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    else -> Unit
                }

                if (touchRegion != TouchRegion.None) {
                    change.consume()
                }
            },
            onUp = {
                touchRegion = TouchRegion.None
                rectTemp = rectDraw.copy()
                onUp(currentTransform, rectDraw)
            },
            key1 = enabled
        )
    },
    inspectorInfo = {
        name = "transform"
        properties["enabled"] = enabled
        properties["size"] = size
        properties["touchRegionRadius"] = touchRegionRadius
        properties["minDimension"] = minDimension
        properties["transform"] = transform
        properties["onDown"] = onDown
        properties["onMove"] = onMove
        properties["onUp"] = onUp
    }
)
