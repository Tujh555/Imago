package io.tujh.imago.presentation.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.IntSize
import io.tujh.imago.domain.post.model.ShortPost

@Immutable
data class ShortPostItem(
    val id: String,
    val url: String,
    val originalSize: IntSize,
    val title: String
)

fun ShortPost.toUi() = ShortPostItem(
    id = id,
    url = firstImage.url,
    originalSize = IntSize(firstImage.originalWidth, firstImage.originalHeight),
    title = title
)