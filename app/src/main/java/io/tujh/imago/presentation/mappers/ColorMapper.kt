package io.tujh.imago.presentation.mappers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt

fun Color.toHex(): String = "#${Integer.toHexString(toArgb()).uppercase()}"

fun String.toColor(): Color = Color(toColorInt())