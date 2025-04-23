package io.tujh.imago.presentation.editor.components.crop.options

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.smarttoolfactory.animatedlist.AnimatedInfiniteLazyRow
import io.tujh.imago.presentation.editor.image.crop.AspectRatioSelectionCard
import io.tujh.imago.presentation.editor.image.crop.CropAspectRatio
import io.tujh.imago.presentation.editor.image.crop.aspectRatios
import kotlinx.coroutines.launch

@Composable
fun AnimatedAspectRatioSelection(
    modifier: Modifier = Modifier,
    initialSelectedIndex: Int = 2,
    onAspectRatioChange: (CropAspectRatio) -> Unit
) {

    var currentIndex by remember { mutableIntStateOf(initialSelectedIndex) }
    val coroutineScope = rememberCoroutineScope()

    AnimatedInfiniteLazyRow(
        modifier = modifier,
        items = aspectRatios,
        inactiveItemPercent = 80,
        initialFirstVisibleIndex = initialSelectedIndex - 2
    ) { animationProgress, _, item, width, lazyListState ->

        val scale = animationProgress.scale
        val color = animationProgress.color
        val selectedLocalIndex = animationProgress.itemIndex

        AspectRatioSelectionCard(modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null
            ) {
                coroutineScope.launch {
                    lazyListState.animateScrollBy(animationProgress.distanceToSelector)
                }
            }
            .width(width),
            contentColor = MaterialTheme.colorScheme.surface,
            color = color,
            cropAspectRatio = item
        )

        if (currentIndex != selectedLocalIndex) {
            currentIndex = selectedLocalIndex
            onAspectRatioChange(aspectRatios[selectedLocalIndex])
        }
    }
}