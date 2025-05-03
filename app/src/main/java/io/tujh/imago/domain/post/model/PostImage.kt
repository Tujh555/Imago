package io.tujh.imago.domain.post.model

data class PostImage(
    val previewUrl: String,
    val fullUrl: String,
    val originalWidth: Int,
    val originalHeight: Int
)