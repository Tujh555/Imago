package io.tujh.imago.presentation.editor.image.zoom

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntRect

@Immutable
data class ZoomData(
    val zoom: Float = 1f,
    val pan: Offset = Offset.Zero,
    val rotation: Float = 0f
)