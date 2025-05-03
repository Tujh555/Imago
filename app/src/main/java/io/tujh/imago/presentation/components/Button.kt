package io.tujh.imago.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import io.tujh.imago.presentation.editor.components.scaffold.IconSource
import io.tujh.imago.presentation.editor.components.scaffold.painter
import io.tujh.imago.presentation.theme.colors.ImagoColors

@Composable
fun Button(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    padding: Dp = 16.dp,
    clickable: Boolean = true,
    backgroundColor: Color = ImagoColors.semitransparent,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor, shape)
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
    backgroundColor: Color = ImagoColors.semitransparent,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        shape = shape,
        padding = padding,
        onClick = onClick,
        clickable = active,
        backgroundColor = backgroundColor
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

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    padding: Dp = 16.dp,
    active: Boolean = true,
    tint: Color = Color.White,
    size: Dp = 20.dp,
    backgroundColor: Color = ImagoColors.semitransparent,
    iconSource: IconSource,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        shape = shape,
        padding = padding,
        onClick = onClick,
        clickable = active,
        backgroundColor = backgroundColor
    ) {
        val iconTint by animateColorAsState(
            targetValue = if (active) tint else tint.copy(alpha = 0.5f)
        )
        Icon(
            modifier = Modifier.size(size),
            painter = iconSource.painter(),
            contentDescription = null,
            tint = iconTint
        )
    }
}