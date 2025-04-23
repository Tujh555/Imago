package io.tujh.imago.presentation.editor.components.crop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import io.tujh.imago.presentation.editor.components.EditingComponent
import io.tujh.imago.presentation.editor.components.scaffold.EditScaffold
import io.tujh.imago.presentation.editor.components.scaffold.alwaysVisibleState
import io.tujh.imago.presentation.editor.components.scaffold.button
import io.tujh.imago.presentation.editor.components.scaffold.controlButtons
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
    private var properties by mutableStateOf(
        CropDefaults.properties(
            cropOutlineProperty = CropOutlineProperty(
                OutlineType.Rect,
                RectCropShape(0, "Rect")
            ),
            handleSize = 30f
        )
    )
    private var style by mutableStateOf(CropDefaults.style())
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
            val requiredSize = properties.requiredSize
            val resized = if (requiredSize != null) {
                cropAgent.resize(cropped, requiredSize.width, requiredSize.height)
            } else {
                cropped
            }
            listener.save(resized)
        }
    }

    private fun settingsSheet(selectionPage: SelectionPage) = SettingsBottomSheet(
        selectionPage = selectionPage,
        currentProperties = properties,
        currentStyle = style,
        onStyleChanged = { style = it },
        onPropertiesChanged = { properties = it }
    )

    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val density = LocalDensity.current
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        Box(modifier = Modifier.fillMaxSize()) {
            EditScaffold(
                modifier = Modifier.fillMaxSize(),
                buttons = controlButtons(
                    close = listener::close,
                    save = { save(scope, density) },
                    central = {
                        button(alwaysVisibleState, Icons.Filled.Settings) {
                            bottomSheetNavigator.show(settingsSheet(SelectionPage.Properties))
                        }
                        button(alwaysVisibleState, brushImage) {
                            bottomSheetNavigator.show(settingsSheet(SelectionPage.Style))
                        }
                    }
                ),
            ) {
                ImageCropper(
                    imageBitmap = bitmap,
                    cropProperties = properties,
                    cropStyle = style,
                    onCropStateChanged = { cropState = it },
                    onImageScaled = { scaledBitmap = it }
                )
            }
        }
    }

    companion object {
        private val brushImage = materialIcon(name = "Filled.Brush") {
            materialPath {
                moveTo(7.0f, 14.0f)
                curveToRelative(-1.66f, 0.0f, -3.0f, 1.34f, -3.0f, 3.0f)
                curveToRelative(0.0f, 1.31f, -1.16f, 2.0f, -2.0f, 2.0f)
                curveToRelative(0.92f, 1.22f, 2.49f, 2.0f, 4.0f, 2.0f)
                curveToRelative(2.21f, 0.0f, 4.0f, -1.79f, 4.0f, -4.0f)
                curveToRelative(0.0f, -1.66f, -1.34f, -3.0f, -3.0f, -3.0f)
                close()
                moveTo(20.71f, 4.63f)
                lineToRelative(-1.34f, -1.34f)
                curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0.0f)
                lineTo(9.0f, 12.25f)
                lineTo(11.75f, 15.0f)
                lineToRelative(8.96f, -8.96f)
                curveToRelative(0.39f, -0.39f, 0.39f, -1.02f, 0.0f, -1.41f)
                close()
            }
        }
    }
}