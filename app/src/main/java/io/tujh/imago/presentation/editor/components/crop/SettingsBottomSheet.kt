package io.tujh.imago.presentation.editor.components.crop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import io.tujh.imago.R
import io.tujh.imago.presentation.components.BottomSheetContent
import io.tujh.imago.presentation.editor.components.crop.options.CropStyleSelectionMenu
import io.tujh.imago.presentation.editor.components.crop.options.PropertySelectionSheet
import io.tujh.imago.presentation.editor.image.crop.CropFrameFactory
import io.tujh.imago.presentation.editor.image.crop.CropProperties
import io.tujh.imago.presentation.editor.image.crop.CropStyle

enum class SelectionPage {
    Properties, Style
}

@Stable
class SettingsBottomSheet(
    private val selectionPage: SelectionPage,
    private val currentProperties: CropProperties,
    private val currentStyle: CropStyle,
    private val onPropertiesChanged: (CropProperties) -> Unit,
    private val onStyleChanged: (CropStyle) -> Unit
) : Screen {
    @Composable
    override fun Content() {
        val backgroundColor = MaterialTheme.colorScheme.surface
        val peekColor = contentColorFor(backgroundColor)
        BottomSheetContent(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(horizontal = 24.dp),
            peekColor = peekColor
        ) {
            var cropProperties by remember { mutableStateOf(currentProperties) }
            var cropStyle by remember { mutableStateOf(currentStyle) }
            val defaultImage1 = ImageBitmap.imageResource(R.drawable.img_squircle)
            val defaultImage2 = ImageBitmap.imageResource(R.drawable.img_cloud)
            val defaultImage3 = ImageBitmap.imageResource(R.drawable.img_sun)

            val cropFrameFactory = remember {
                CropFrameFactory(listOf(defaultImage1, defaultImage2, defaultImage3))
            }

            LaunchedEffect(cropProperties) { onPropertiesChanged(cropProperties) }
            LaunchedEffect(cropStyle) { onStyleChanged(cropStyle) }

            if (selectionPage == SelectionPage.Properties) {
                PropertySelectionSheet(
                    cropFrameFactory = cropFrameFactory,
                    cropProperties = cropProperties,
                    onCropPropertiesChange = { cropProperties = it }
                )
            } else {
                CropStyleSelectionMenu(
                    cropType = cropProperties.cropType,
                    cropStyle = cropStyle,
                    onCropStyleChange = { cropStyle = it }
                )
            }
        }
    }
}