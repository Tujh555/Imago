package io.tujh.imago.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.tujh.imago.presentation.theme.colors.ImagoColors

@Composable
fun Button(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    padding: Dp = 16.dp,
    clickable: Boolean = true,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(ImagoColors.semitransparent, shape)
            .clickable(enabled = clickable, onClick = onClick)
            .padding(padding),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    padding: Dp = 16.dp,
    active: Boolean = true,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        shape = shape,
        padding = padding,
        onClick = onClick,
        clickable = active
    ) {
        val color by animateColorAsState(
            targetValue = if (active) textColor else textColor.copy(alpha = 0.5f)
        )
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}