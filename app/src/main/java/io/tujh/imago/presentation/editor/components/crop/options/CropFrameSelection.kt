package io.tujh.imago.presentation.editor.components.crop.options

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.smarttoolfactory.animatedlist.AnimatedInfiniteLazyRow
import com.smarttoolfactory.animatedlist.model.AnimationProgress
import io.tujh.imago.presentation.editor.image.crop.AspectRatio
import io.tujh.imago.presentation.editor.image.crop.CropFrame
import io.tujh.imago.presentation.editor.image.crop.CropFrameDisplayCard
import io.tujh.imago.presentation.editor.image.crop.CropFrameFactory
import io.tujh.imago.presentation.editor.image.crop.CropOutlineProperty
import io.tujh.imago.presentation.editor.image.crop.OutlineType
import kotlinx.coroutines.launch

@Composable
fun CropFrameSelection(
    aspectRatio: AspectRatio,
    cropFrameFactory: CropFrameFactory,
    cropOutlineProperty: CropOutlineProperty,
    conCropOutlinePropertyChange: (CropOutlineProperty) -> Unit
) {

    var showEditDialog by remember { mutableStateOf(false) }

    var cropFrame by remember {
        mutableStateOf(
            cropFrameFactory.getCropFrame(cropOutlineProperty.outlineType)
        )
    }

    if (showEditDialog) {
        CropFrameListDialog(
            aspectRatio = aspectRatio,
            cropFrame = cropFrame,
            onConfirm = {
                cropFrame = it
                cropFrameFactory.editCropFrame(cropFrame)

                conCropOutlinePropertyChange(
                    CropOutlineProperty(
                        it.outlineType,
                        it.cropOutlineContainer.selectedItem
                    )
                )
                showEditDialog = false
            },
            onDismiss = {
                showEditDialog = false
            }
        )
    }

    val initialIndex = remember {
        OutlineType.entries.indexOfFirst { it == cropOutlineProperty.outlineType }
    }

    CropFrameSelectionList(
        modifier = Modifier.fillMaxWidth(),
        cropFrames = cropFrameFactory.getCropFrames(),
        initialSelectedIndex = initialIndex,
        onClick = {
            cropFrame = it
            showEditDialog = true
        },
        onCropFrameChange = {
            conCropOutlinePropertyChange(
                CropOutlineProperty(
                    it.outlineType,
                    it.cropOutlineContainer.selectedItem
                )
            )
        }
    )
}

@Composable
private fun CropFrameSelectionList(
    modifier: Modifier = Modifier,
    initialSelectedIndex: Int = 0,
    cropFrames: List<CropFrame>,
    onClick: (CropFrame) -> Unit,
    onCropFrameChange: (CropFrame) -> Unit
) {

    var currentIndex by remember { mutableIntStateOf(initialSelectedIndex) }
    val coroutineScope = rememberCoroutineScope()

    AnimatedInfiniteLazyRow(
        modifier = modifier,
        items = cropFrames,
        inactiveItemPercent = 80,
        initialFirstVisibleIndex = initialSelectedIndex - 2,
    ) { animationProgress: AnimationProgress,
        _: Int,
        item: CropFrame,
        width: Dp,
        lazyListState ->

        val scale = animationProgress.scale
        val color = animationProgress.color

        val selectedLocalIndex = animationProgress.itemIndex
        val cropOutline = item.cropOutlineContainer.selectedItem

        val editable = item.editable

        CropFrameDisplayCard(
            modifier = Modifier
                .width(width)
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null
                ) {
                    coroutineScope.launch {
                        lazyListState.animateScrollBy(animationProgress.distanceToSelector)
                    }
                },
            editable = editable,
            scale = scale,
            outlineColor = color,
            title = cropOutline.title,
            cropOutline = cropOutline
        ) {
            onClick(item)
        }

        if (currentIndex != selectedLocalIndex) {
            currentIndex = selectedLocalIndex
            onCropFrameChange(cropFrames[selectedLocalIndex])
        }
    }
}
