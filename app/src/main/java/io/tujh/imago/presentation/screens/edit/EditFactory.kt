package io.tujh.imago.presentation.screens.edit

import androidx.compose.ui.graphics.ImageBitmap
import io.tujh.imago.R
import io.tujh.imago.presentation.editor.components.EditingComponent
import io.tujh.imago.presentation.editor.components.crop.CropComponent
import io.tujh.imago.presentation.editor.components.draw.DrawComponent

enum class EditFactory(
    val icon: Int,
    private val factory: (ImageBitmap, String, EditingComponent.Saver) -> EditingComponent
) {
    Draw(R.drawable.ic_pencil, ::DrawComponent),
    Crop(R.drawable.ic_crop, ::CropComponent);

    operator fun invoke(bitmap: ImageBitmap, sharedKey: String, saver: EditingComponent.Saver) =
        factory(bitmap, sharedKey, saver)
}

