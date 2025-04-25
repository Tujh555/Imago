package io.tujh.imago.presentation.editor.components.draw

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class DrawSettings(
    val size: Float,
    val selectedColor: Color,
    val opacity: Float,
    val availableColors: List<Color>,
    val brushFactory: BrushFactory
) {
    val selectedColorIndex = availableColors.indexOf(selectedColor)

    companion object {
        private val defaultColors = listOf(
            Color(color = 0xFFEA5E3A),
            Color(color = 0xFF7FA4A1),
            Color(color = 0xFF3D654E),
            Color(color = 0xFFA7A377),
            Color(color = 0xFFD8AF9F),
            Color(color = 0xFF3C7C91),
            Color(color = 0xFFE0D6CD),
            Color(color = 0xFFE0AA92),
            Color(color = 0xFF49A6B8),
            Color(color = 0xFF84514E)
        )

        val default = DrawSettings(
            size = 20.0f,
            selectedColor = defaultColors.first(),
            opacity = 1.0f,
            availableColors = defaultColors,
            brushFactory = BrushFactory.Basic
        )
    }
}