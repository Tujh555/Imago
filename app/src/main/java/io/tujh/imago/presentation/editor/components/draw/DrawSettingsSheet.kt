package io.tujh.imago.presentation.editor.components.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.smarttoolfactory.animatedlist.AnimatedInfiniteLazyRow
import com.smarttoolfactory.colorpicker.dialog.ColorPickerRingDiamondHSLDialog
import io.tujh.imago.presentation.editor.components.crop.options.BaseSheet
import io.tujh.imago.presentation.editor.components.crop.options.ColorSelection
import io.tujh.imago.presentation.editor.components.crop.options.DialogWithMultipleSelection
import io.tujh.imago.presentation.editor.components.crop.options.MaxZoomSelection
import io.tujh.imago.presentation.editor.components.crop.options.SliderSelection
import io.tujh.imago.presentation.editor.components.crop.options.Title
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DrawSettingsSheet(
    private val currentSettings: DrawSettings,
    private val onChanged: (DrawSettings) -> Unit
) : Screen {
    @Composable
    override fun Content() {
        var currentSettings by remember { mutableStateOf(currentSettings) }

        val backgroundColor = MaterialTheme.colorScheme.surface
        val peekColor = contentColorFor(backgroundColor)

        LaunchedEffect(currentSettings) { onChanged(currentSettings) }

        Box {
            var showColorDialog by remember { mutableStateOf(false) }
            BaseSheet(peekColor) {
                Title("Brush Type")
                BrushFactorySelection(currentSettings.brushFactory) { selected ->
                    currentSettings = currentSettings.copy(
                        brushFactory = selected
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Title("Color")
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { showColorDialog = true },
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = peekColor
                    )
                }

                AvailableColorSelection(
                    selectedIndex = currentSettings.selectedColorIndex,
                    colors = currentSettings.availableColors,
                    onSelected = { selected ->
                        currentSettings = currentSettings.copy(selectedColor = selected)
                    }
                )

                Title("Brush Size ${currentSettings.size}px", fontSize = 16.sp)
                SliderSelection(
                    value = currentSettings.size,
                    onValueChange = {
                        currentSettings = currentSettings.copy(
                            size = (it * 100f).roundToInt() / 100f
                        )
                    },
                    valueRange = 10f..150f
                )

                Title("Opacity ${currentSettings.opacity}px", fontSize = 16.sp)

                SliderSelection(
                    value = currentSettings.opacity,
                    onValueChange = {
                        currentSettings = currentSettings.copy(
                            opacity = (it * 100f).roundToInt() / 100f
                        )
                    },
                    valueRange = 0f..1f
                )
            }
            if (showColorDialog) {
                ColorPickerRingDiamondHSLDialog(
                    initialColor = currentSettings.selectedColor,
                    onDismiss = { colorChange, _ ->
                        currentSettings = currentSettings.copy(
                            availableColors = currentSettings.availableColors + colorChange,
                            selectedColor = colorChange
                        )
                        showColorDialog = false
                    }
                )
            }
        }
    }

    @Composable
    private fun BrushFactorySelection(
        current: BrushFactory,
        onSelected: (BrushFactory) -> Unit
    ) {
        var showDialog by remember { mutableStateOf(false) }

        Text(
            text = current.name,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
                .padding(8.dp)

        )

        if (showDialog) {
            DialogWithMultipleSelection(
                title = "Brush Type",
                options = availableFactoriesNames,
                value = current.ordinal,
                onDismiss = { showDialog = false },
                onConfirm = {
                    onSelected(availableFactories[it])
                    showDialog = false
                }
            )
        }
    }

    @Composable
    private fun AvailableColorSelection(
        selectedIndex: Int,
        colors: List<Color>,
        onSelected: (Color) -> Unit
    ) {
        val scope = rememberCoroutineScope()
        var currentIndex by remember { mutableIntStateOf(selectedIndex) }

        AnimatedInfiniteLazyRow(
            items = colors,
            inactiveItemPercent = 60,
            initialFirstVisibleIndex = selectedIndex - 2
        ) { progress, _, color, width, lazyListState ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .graphicsLayer {
                        scaleX = progress.scale
                        scaleY = progress.scale
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            scope.launch {
                                lazyListState.animateScrollBy(progress.distanceToSelector)
                            }
                        }
                    )
                    .size(width)
                    .background(color, CircleShape)
            )

            if (currentIndex != progress.itemIndex) {
                currentIndex = progress.itemIndex
                onSelected(colors[progress.itemIndex])
            }
        }
    }

    companion object {
        private val availableFactories = BrushFactory.entries.filter { it != BrushFactory.Eraser }
        private val availableFactoriesNames = availableFactories.map(BrushFactory::name)
    }
}