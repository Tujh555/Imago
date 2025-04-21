package io.tujh.imago.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(color = Colors.semitransparent, shape = shape)
            .clickable(onClick = onClick)
            .padding(10.dp),
        content = content
    )
}

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    iconTint: Color = Color.White,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
) {
    IconButton(modifier, shape, onClick) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = iconTint
        )
    }
}