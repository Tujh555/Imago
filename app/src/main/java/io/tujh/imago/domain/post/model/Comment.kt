package io.tujh.imago.domain.post.model

import io.tujh.imago.domain.user.User
import java.time.Instant

data class Comment(
    val id: String,
    val author: User,
    val createdAt: Instant,
    val text: String
)