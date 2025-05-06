package io.tujh.imago.domain.post.model

import java.time.Instant

// TODO переименовать в Post и добавить список картинок
data class ShortPost(
    val id: String,
    val firstImage: PostImage,
    val title: String,
    val createdAt: Instant,
)