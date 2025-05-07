package io.tujh.imago.domain.post.model

import java.time.Instant

data class Post(
    val id: String,
    val images: List<PostImage>,
    val title: String,
    val createdAt: Instant,
)