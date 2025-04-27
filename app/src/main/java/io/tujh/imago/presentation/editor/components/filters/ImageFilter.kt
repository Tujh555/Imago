package io.tujh.imago.presentation.editor.components.filters

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.unit.IntSize

interface ImageFilter {
    fun onSizeChanged(size: IntSize)

    context(GraphicsLayerScope)
    fun apply()

    @Composable
    fun Controls()
}