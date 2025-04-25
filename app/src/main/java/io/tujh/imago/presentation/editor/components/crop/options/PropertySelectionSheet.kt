package io.tujh.imago.presentation.editor.components.crop.options

import androidx.compose.runtime.Composable
import io.tujh.imago.presentation.editor.image.crop.CropFrameFactory
import io.tujh.imago.presentation.editor.image.crop.CropProperties

@Composable
fun PropertySelectionSheet(
    cropFrameFactory: CropFrameFactory,
    cropProperties: CropProperties,
    onCropPropertiesChange: (CropProperties) -> Unit
) {
    CropPropertySelectionMenu(
        cropFrameFactory = cropFrameFactory,
        cropProperties = cropProperties,
        onCropPropertiesChange = onCropPropertiesChange
    )

    CropGestureSelectionMenu(
        cropProperties = cropProperties,
        onCropPropertiesChange = onCropPropertiesChange
    )
}
