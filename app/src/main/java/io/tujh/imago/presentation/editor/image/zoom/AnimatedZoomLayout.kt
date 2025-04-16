package io.tujh.imago.presentation.editor.image.zoom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.util.fastForEach
import io.tujh.imago.presentation.editor.image.SlotsEnum

@Composable
fun AnimatedZoomLayout(
    modifier: Modifier = Modifier,
    clip: Boolean = true,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 3f,
    fling: Boolean = true,
    moveToBounds: Boolean = false,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = true,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = DefaultOnDoubleTap,
    content: @Composable () -> Unit
) {
    AnimatedZoomSubcomposeLayout(
        modifier = modifier,
        mainContent = { content() }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .animatedZoom(
                    enabled = enabled,
                    clip = clip,
                    zoomOnDoubleTap = zoomOnDoubleTap,
                    animatedZoomState = rememberAnimatedZoomState(
                        minZoom = minZoom,
                        maxZoom = maxZoom,
                        initialZoom = initialZoom,
                        fling = fling,
                        moveToBounds = moveToBounds,
                        zoomable = zoomable,
                        pannable = pannable,
                        rotatable = rotatable,
                        limitPan = limitPan,
                        contentSize = it
                    ),
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
private fun AnimatedZoomSubcomposeLayout(
    modifier: Modifier = Modifier,
    mainContent: @Composable () -> Unit,
    dependentContent: @Composable (DpSize) -> Unit
) {
    val density = LocalDensity.current

    SubcomposeLayout(modifier) { constraints ->
        val mainPlaceables: List<Placeable> = subcompose(SlotsEnum.Main, mainContent).map {
            it.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }

        var maxWidth = 0
        var maxHeight = 0

        mainPlaceables.fastForEach { placeable: Placeable ->
            maxWidth += placeable.width
            maxHeight = placeable.height
        }

        val dependentPlaceables = with(density) {
            subcompose(SlotsEnum.Dependent) { dependentContent(DpSize(maxWidth.toDp(), maxHeight.toDp())) }
                .map { measurable: Measurable ->
                    measurable.measure(constraints)
                }
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            dependentPlaceables.fastForEach { placeable: Placeable ->
                placeable.placeRelative(0, 0)
            }
        }
    }
}
