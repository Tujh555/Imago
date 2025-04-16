package io.tujh.imago.presentation.editor.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.util.fastForEach

@Composable
fun DimensionSubcomposeLayout(
    modifier: Modifier = Modifier,
    placeMainContent: Boolean = true,
    mainContent: @Composable () -> Unit,
    dependentContent: @Composable (Size) -> Unit
) {
    SubcomposeLayout(modifier) { constraints ->

        val mainPlaceables = subcompose(SlotsEnum.Main, mainContent).map {
            it.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }

        var maxWidth = 0
        var maxHeight = 0

        mainPlaceables.fastForEach { placeable ->
            maxWidth += placeable.width
            maxHeight = placeable.height
        }

        val dependentPlaceables = subcompose(SlotsEnum.Dependent) {
            dependentContent(Size(maxWidth.toFloat(), maxHeight.toFloat()))
        }.map { measurable -> measurable.measure(constraints) }


        layout(maxWidth, maxHeight) {
            if (placeMainContent) {
                mainPlaceables.fastForEach { placeable ->
                    placeable.placeRelative(0, 0)
                }
            }

            dependentPlaceables.fastForEach { placeable ->
                placeable.placeRelative(0, 0)
            }
        }
    }
}

