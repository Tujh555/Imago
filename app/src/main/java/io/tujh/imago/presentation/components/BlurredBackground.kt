package io.tujh.imago.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val backgroundBrush = Brush.verticalGradient(
    listOf(Color(0xFF212121), Color(0xFF424242))
)

@Composable
@NonRestartableComposable
fun BlurredBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.matchParentSize().background(backgroundBrush).blur(10.dp))
        content()
    }
}