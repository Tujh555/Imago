package io.tujh.imago.presentation.theme.colors

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ImagoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme(), content = content)
}

object ImagoColors {
    val purple = Color(0xFF3B3759)
    val pink = Color(0xFFAA5377)
    val lightPink = Color(0xFFCF8DA7)
    val dustyBlue = Color(0xFF4C6E81)
    val lightDustyBlue = Color(0xFF7AACB3)
    val semitransparent = Color(0xFF111111).copy(alpha = 0.6f)
    val red = Color(0xFFE60022)
}