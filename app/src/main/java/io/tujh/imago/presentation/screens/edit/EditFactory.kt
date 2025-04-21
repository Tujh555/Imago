package io.tujh.imago.presentation.screens.edit

import androidx.compose.ui.graphics.ImageBitmap
import io.tujh.imago.R
import io.tujh.imago.presentation.editor.components.EditingComponent
import io.tujh.imago.presentation.editor.components.draw.DrawComponent

enum class EditFactory(
    val icon: Int,
    private val factory: (ImageBitmap, EditingComponent.FinishListener) -> EditingComponent
) {
    Draw(R.drawable.ic_pencil, ::DrawComponent);

    operator fun invoke(bitmap: ImageBitmap, listener: EditingComponent.FinishListener) =
        factory(bitmap, listener)
}

