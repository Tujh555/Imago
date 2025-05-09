package io.tujh.imago.presentation.models

import androidx.compose.runtime.Immutable
import io.tujh.imago.domain.post.model.Post
import io.tujh.imago.domain.post.model.PostImage

@Immutable
data class PostItem(
    val id: String,
    val images: List<PostImageItem>,
    val title: String,
)

@Immutable
data class PostImageItem(
    val url: String,
    val width: Int,
    val height: Int
)

fun PostImage.toUi() = PostImageItem(url, originalWidth, originalHeight)

fun Post.toUi() = PostItem(
    id = id,
    images = images.map(PostImage::toUi),
    title = title
)