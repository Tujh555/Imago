package io.tujh.imago.presentation.editor.image.transform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.tujh.imago.presentation.editor.image.SlotsEnum

@Composable
fun MorphSubcomposeLayout(
    modifier: Modifier = Modifier,
    handleRadius: Dp = 15.dp,
    updatePhysicalSize: Boolean = false,
    mainContent: @Composable () -> Unit,
    dependentContent: @Composable (IntSize, Constraints) -> Unit
) {

    val handleRadiusInPx = with(LocalDensity.current) {
        handleRadius.roundToPx()
    }

    SubcomposeLayout(modifier = modifier) { constraints ->
        val mainPlaceables = subcompose(SlotsEnum.Main, mainContent).map {
            it.measure(constraints)
        }

        var maxWidth = 0
        var maxHeight = 0

        mainPlaceables.fastForEach { placeable: Placeable ->
            maxWidth += placeable.width
            maxHeight = placeable.height
        }

        val handleSize = handleRadiusInPx * 2
        maxWidth = maxWidth.coerceAtMost(constraints.maxWidth - handleSize)
        maxHeight = maxHeight.coerceAtMost(constraints.maxHeight - handleSize)

        val maxSize = IntSize(maxWidth, maxHeight)

        val dependentPlaceables = subcompose(SlotsEnum.Dependent) {
            dependentContent(maxSize, constraints)
        }.map { it.measure(constraints) }

        val dependentMaxSize = dependentPlaceables.fold(IntSize.Zero) { currentMax, placeable ->
            IntSize(
                width = maxOf(currentMax.width, placeable.width),
                height = maxOf(currentMax.height, placeable.height)
            )
        }

        val width: Int
        val height: Int
        if (updatePhysicalSize) {
            width = dependentMaxSize.width
            height = dependentMaxSize.height

        } else {
            width = maxSize.width + 2 * handleRadiusInPx
            height = maxSize.height + 2 * handleRadiusInPx
        }

        layout(width, height) {
            dependentPlaceables.fastForEach { placeable: Placeable ->
                placeable.placeRelative(0, 0)
            }
        }
    }
}
