package io.tujh.imago.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    url: String?,
    userId: String,
    shape: Shape = CircleShape,
    onClick: (() -> Unit)? = null
) {
    val targetUrl = url ?: "file:///android_asset/avatar_${userId.uuidIndex() % 50 + 1}.webp"
    AsyncImage(
        modifier = modifier.clip(shape).applyNotNull(onClick) { clickable(onClick = it) },
        contentDescription = null,
        model = targetUrl,
        contentScale = ContentScale.Crop,
    )
}

fun String.uuidIndex(): Long {
    if (length < 8) return 0

    return runCatching { java.lang.Long.parseLong(this, 0, 8, 36) }
        .getOrDefault(0)
}