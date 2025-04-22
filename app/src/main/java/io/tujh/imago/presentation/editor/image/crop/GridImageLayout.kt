package io.tujh.imago.presentation.editor.image.crop

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap

@Composable
fun GridImageLayout(
    modifier: Modifier = Modifier,
    thumbnails: List<Int>,
    divider: Dp = 2.dp,
    onClick: ((List<Int>) -> Unit)? = null
) {
    if (thumbnails.isNotEmpty()) {

        ImageDrawLayout(
            modifier = modifier.clickable { onClick?.invoke(thumbnails) },
            divider = divider,
            itemCount = thumbnails.size
        ) {

            val size = thumbnails.size
            if (size < 5) {
                thumbnails.forEach {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = "Icon",
                        contentScale = ContentScale.Crop,
                    )
                }
            } else {
                thumbnails.take(3).forEach { it ->
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = "Icon",
                        contentScale = ContentScale.Crop,
                    )

                }

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    val carry = size - 3
                    Text(text = "+$carry", fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
private fun ImageDrawLayout(
    modifier: Modifier = Modifier,
    itemCount: Int,
    divider: Dp,
    content: @Composable () -> Unit
) {

    val spacePx = LocalDensity.current.run { (divider).roundToPx() }

    val measurePolicy = remember(itemCount, spacePx) {
        MeasurePolicy { measurables, constraints ->

            val newConstraints = when (itemCount) {
                1 -> constraints
                2 -> Constraints.fixed(
                    width = constraints.maxWidth / 2 - spacePx / 2,
                    height = constraints.maxHeight
                )
                else -> Constraints.fixed(
                    width = constraints.maxWidth / 2 - spacePx / 2,
                    height = constraints.maxHeight / 2 - spacePx / 2
                )
            }

            val placeables: List<Placeable> = if (measurables.size != 3) {
                measurables.fastMap { measurable: Measurable ->
                    measurable.measure(constraints = newConstraints)
                }
            } else {
                measurables
                    .take(2)
                    .fastMap { measurable: Measurable ->
                        measurable.measure(constraints = newConstraints)
                    } +
                        measurables
                            .last()
                            .measure(
                                constraints = Constraints.fixed(
                                    constraints.maxWidth,
                                    constraints.maxHeight / 2 - spacePx
                                )
                            )
            }

            layout(constraints.maxWidth, constraints.maxHeight) {
                when (itemCount) {
                    1 -> {
                        placeables.fastForEach { placeable: Placeable ->
                            placeable.placeRelative(0, 0)
                        }
                    }
                    2 -> {
                        var xPos = 0
                        placeables.fastForEach { placeable: Placeable ->
                            placeable.placeRelative(xPos, 0)
                            xPos += placeable.width + spacePx
                        }
                    }
                    else -> {
                        var xPos = 0
                        var yPos = 0

                        placeables.fastForEachIndexed { index: Int, placeable: Placeable ->
                            placeable.placeRelative(xPos, yPos)

                            if (index % 2 == 0) {
                                xPos += placeable.width + spacePx
                            } else {
                                xPos = 0
                            }

                            if (index % 2 == 1) {
                                yPos += placeable.height + spacePx
                            }
                        }
                    }
                }
            }
        }
    }

    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = measurePolicy
    )
}
