package io.tujh.imago.presentation.editor.components.crop.options

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.tujh.imago.presentation.editor.image.crop.AspectRatio
import io.tujh.imago.presentation.editor.image.crop.CropAspectRatio
import io.tujh.imago.presentation.editor.image.crop.CropFrameFactory
import io.tujh.imago.presentation.editor.image.crop.CropProperties
import io.tujh.imago.presentation.editor.image.crop.CropType
import io.tujh.imago.presentation.editor.image.crop.aspectRatios
import kotlin.math.roundToInt

@Composable
fun CropPropertySelectionMenu(
    cropFrameFactory: CropFrameFactory,
    cropProperties: CropProperties,
    onCropPropertiesChange: (CropProperties) -> Unit
) {

    val density = LocalDensity.current

    val cropType = cropProperties.cropType
    val aspectRatio = cropProperties.aspectRatio

    val handleSize = density.run { cropProperties.handleSize.toDp() }
    val contentScale = cropProperties.contentScale
    val cropOutlineProperty = cropProperties.cropOutlineProperty
    val overlayRatio = (cropProperties.overlayRatio * 100)

    Title("Crop Type")
    CropTypeDialogSelection(
        cropType = cropType,
        onCropTypeChange = { cropTypeChange ->
            onCropPropertiesChange(
                cropProperties.copy(cropType = cropTypeChange)
            )
        }
    )

    Title("Content Scale")
    ContentScaleDialogSelection(contentScale) {
        onCropPropertiesChange(
            cropProperties.copy(contentScale = it)
        )
    }

    Title("Aspect Ratio")
    AspectRatioSelection(
        aspectRatio = aspectRatio,
        onAspectRatioChange = {
            onCropPropertiesChange(
                cropProperties.copy(aspectRatio = it.aspectRatio)
            )
        }
    )

    Title("Fix aspect ratio")
    FixedAspectRatioEnableSelection(
        fixedAspectRatioEnabled = cropProperties.fixedAspectRatio,
        onFixedAspectRatioChanged = {
            onCropPropertiesChange(
                cropProperties.copy(fixedAspectRatio = it)
            )
        }
    )

    Title("Frame")
    CropFrameSelection(
        aspectRatio = aspectRatio,
        cropFrameFactory = cropFrameFactory,
        cropOutlineProperty = cropOutlineProperty,
        conCropOutlinePropertyChange = {
            onCropPropertiesChange(
                cropProperties.copy(cropOutlineProperty = it)
            )
        }
    )

    Title("Overlay Ratio ${overlayRatio.toInt()}%")
    SliderSelection(
        value = overlayRatio, valueRange = 50f..100f
    ) {
        onCropPropertiesChange(
            cropProperties.copy(overlayRatio = (it.toInt() / 100f))
        )
    }

    if (cropType == CropType.Dynamic) {
        Title("Handle Size")
        DpSliderSelection(
            value = handleSize,
            onValueChange = {
                onCropPropertiesChange(
                    cropProperties.copy(handleSize = density.run { it.toPx() })
                )
            },
            lowerBound = 10.dp,
            upperBound = 40.dp
        )
    }
}

@Composable
fun CropGestureSelectionMenu(
    cropProperties: CropProperties,
    onCropPropertiesChange: (CropProperties) -> Unit
) {
    val flingEnabled = cropProperties.fling
    val pannable = cropProperties.pannable
    val zoomable = cropProperties.zoomable
    val maxZoom = cropProperties.maxZoom

    Title("Pan Enabled")
    PanEnableSelection(
        panEnabled = pannable,
        onPanEnabledChange = {
            onCropPropertiesChange(
                cropProperties.copy(pannable = it)
            )
        }
    )

    Title("Fling")
    FlingEnableSelection(
        flingEnabled = flingEnabled,
        onFlingEnabledChange = {
            onCropPropertiesChange(
                cropProperties.copy(fling = it)
            )
        }
    )

    Title("Zoom Enabled")
    ZoomEnableSelection(
        zoomEnabled = zoomable,
        onZoomEnabledChange = {
            onCropPropertiesChange(
                cropProperties.copy(zoomable = it)
            )
        }
    )

    AnimatedVisibility(visible = zoomable) {
        Column {

            Title("Max Zoom ${maxZoom}x", fontSize = 16.sp)
            MaxZoomSelection(
                maxZoom = maxZoom,
                onMaxZoomChange = {

                    val max = (it * 100f).roundToInt() / 100f
                    onCropPropertiesChange(
                        cropProperties.copy(maxZoom = max)
                    )
                },
                valueRange = 1f..10f
            )
        }
    }
}

@Composable
fun AspectRatioSelection(
    aspectRatio: AspectRatio,
    onAspectRatioChange: (CropAspectRatio) -> Unit
) {

    val initialSelectedIndex = remember {
        val aspectRatios = aspectRatios
        val aspectRatioModel = aspectRatios.first { it.aspectRatio.value == aspectRatio.value }
        aspectRatios.indexOf(aspectRatioModel)
    }

    AnimatedAspectRatioSelection(
        modifier = Modifier.fillMaxWidth(),
        initialSelectedIndex = initialSelectedIndex
    ) {
        onAspectRatioChange(it)
    }
}

@Composable
fun FlingEnableSelection(
    flingEnabled: Boolean,
    onFlingEnabledChange: (Boolean) -> Unit
) {
    FullRowSwitch(
        label = "Enable fling gesture",
        state = flingEnabled,
        onStateChange = onFlingEnabledChange
    )

}

@Composable
fun FixedAspectRatioEnableSelection(
    fixedAspectRatioEnabled: Boolean,
    onFixedAspectRatioChanged: (Boolean) -> Unit
) {
    FullRowSwitch(
        label = "Enable fixed aspect ratio",
        state = fixedAspectRatioEnabled,
        onStateChange = onFixedAspectRatioChanged
    )
}

@Composable
fun PanEnableSelection(
    panEnabled: Boolean,
    onPanEnabledChange: (Boolean) -> Unit
) {
    FullRowSwitch(
        label = "Enable pan gesture",
        state = panEnabled,
        onStateChange = onPanEnabledChange
    )
}

@Composable
fun ZoomEnableSelection(
    zoomEnabled: Boolean,
    onZoomEnabledChange: (Boolean) -> Unit
) {
    FullRowSwitch(
        label = "Enable zoom gesture",
        state = zoomEnabled,
        onStateChange = onZoomEnabledChange
    )
}

@Composable
fun MaxZoomSelection(
    maxZoom: Float,
    onMaxZoomChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    SliderSelection(
        value = maxZoom,
        onValueChange = onMaxZoomChange,
        valueRange = valueRange
    )
}
