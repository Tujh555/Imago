package io.tujh.imago.presentation.editor.image.util

import androidx.compose.ui.geometry.Offset

fun Offset.coerceIn(
    horizontalRange: ClosedRange<Float>,
    verticalRange: ClosedRange<Float>
) = Offset(this.x.coerceIn(horizontalRange), this.y.coerceIn(verticalRange))