package io.tujh.imago.presentation.editor.components.crop

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.tujh.imago.presentation.editor.components.EditScaffold
import io.tujh.imago.presentation.editor.components.EditingComponent
import io.tujh.imago.presentation.editor.image.crop.CropAgent
import io.tujh.imago.presentation.editor.image.crop.CropDefaults
import io.tujh.imago.presentation.editor.image.crop.CropOutlineProperty
import io.tujh.imago.presentation.editor.image.crop.CropState
import io.tujh.imago.presentation.editor.image.crop.ImageCropper
import io.tujh.imago.presentation.editor.image.crop.OutlineType
import io.tujh.imago.presentation.editor.image.crop.RectCropShape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CropComponent(
    private val bitmap: ImageBitmap,
    private val listener: EditingComponent.FinishListener
) : EditingComponent {
    private val cropAgent = CropAgent()
    private var cropState: CropState? = null
    private val properties = CropDefaults.properties(
        cropOutlineProperty = CropOutlineProperty(
            OutlineType.Rect,
            RectCropShape(0, "Rect")
        ),
        handleSize = 30f
    )
    private var scaledBitmap: ImageBitmap? = null

    private fun save(scope: CoroutineScope, density: Density) {
        val state = cropState
        val scaledImage = scaledBitmap
        if (state == null || scaledImage == null) {
            listener.save(bitmap)
            return
        }

        scope.launch(Dispatchers.Default) {
            val cropped = cropAgent.crop(
                imageBitmap = scaledImage,
                cropRect = state.cropRect,
                cropOutline = properties.cropOutlineProperty.cropOutline,
                layoutDirection = LayoutDirection.Ltr,
                density = density
            )
            val resized = if (properties.requiredSize != null) {
                cropAgent.resize(
                    cropped,
                    properties.requiredSize.width,
                    properties.requiredSize.height,
                )
            } else {
                cropped
            }
            listener.save(resized)
        }
    }

    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val density = LocalDensity.current
        EditScaffold(
            modifier = Modifier.fillMaxSize(),
            undo = { },
            undoVisible = false,
            save = { save(scope, density) },
            close = { listener.close() },
        ) {
            ImageCropper(
                imageBitmap = bitmap,
                cropProperties = properties,
                onCropStateChanged = { cropState = it },
                onImageScaled = { scaledBitmap = it }
            )
        }
    }
}