package io.tujh.imago.presentation.editor.components.crop.options

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.slider.ColorfulSlider
import com.smarttoolfactory.slider.MaterialSliderColors
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor

@Composable
fun DpSliderSelection(
    value: Dp,
    onValueChange: (Dp) -> Unit,
    lowerBound: Dp,
    upperBound: Dp
) {
    LocalDensity.current.run {
        val strokeWidthPx = value.toPx()
        val lowerBoundPx = lowerBound.toPx()
        val upperBoundPx = upperBound.toPx()

        SliderSelection(
            value = strokeWidthPx,
            onValueChange = { onValueChange(it.toDp()) },
            valueRange = lowerBoundPx..upperBoundPx
        )
    }
}

@Composable
fun SliderWithValueSelection(
    modifier: Modifier = Modifier,
    value: Float,
    title: String = "",
    text: String,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    colors: MaterialSliderColors = MaterialSliderDefaults.materialColors(
        activeTrackColor = SliderBrushColor(MaterialTheme.colorScheme.primary),
        inactiveTrackColor = SliderBrushColor(Color.Transparent)
    ),
) {
    Column {

        Text(
            text = if (title.isNotEmpty()) "$title $text" else text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColorfulSlider(
                modifier = Modifier.weight(1f),
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                colors = colors,
                trackHeight = 10.dp,
                thumbRadius = 10.dp
            )
        }
    }
}

@Composable
fun SliderSelection(
    modifier: Modifier = Modifier,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    colors: MaterialSliderColors = MaterialSliderDefaults.materialColors(
        activeTrackColor = SliderBrushColor(MaterialTheme.colorScheme.primary),
        inactiveTrackColor = SliderBrushColor(Color.Transparent)
    ),
    onValueChangeFinished: (() -> Unit)? = null,
    onValueChange: (Float) -> Unit,
) {
    ColorfulSlider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        colors = colors,
        borderStroke = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        trackHeight = 10.dp,
        thumbRadius = 10.dp,
        onValueChangeFinished = onValueChangeFinished
    )
}

@Composable
fun Title(
    text: String,
    fontSize: TextUnit = 20.sp
) {
    Text(
        modifier = Modifier.padding(vertical = 1.dp),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold
    )
}


@Composable
fun FullRowSwitch(
    label: String,
    state: Boolean,
    onStateChange: (Boolean) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            role = Role.Switch,
            onClick = {
                onStateChange(!state)
            }
        )
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text = label, modifier = Modifier.weight(1f))

        Switch(
            checked = state,
            onCheckedChange = null
        )
    }
}

@Composable
fun CropTextField(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
fun DialogWithMultipleSelection(
    title: String = "",
    options: List<String>,
    value: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {

    val (selectedOption: Int, onOptionSelected: (Int) -> Unit) = remember {
        mutableIntStateOf(value)
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(Modifier.selectableGroup()) {
                options.forEachIndexed { index, text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (index == selectedOption),
                                onClick = { onOptionSelected(index) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (index == selectedOption),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = text,
                            fontSize = 18.sp,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedOption)
                }
            ) {
                Text(text = "Accept")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = "Cancel")
            }
        }
    )
}